package com.yechat.notification.message;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.yechat.notification.JwtAuthenticationTestConfiguration;
import com.yechat.notification.rsocket.RSocketResponder;
import com.yechat.notification.rsocket.jwt.AuthenticationMimeType;
import com.yechat.notification.rsocket.jwt.JwtRSocketRequesterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessageHandlerCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(
        properties = {
                "spring.rsocket.server.mapping-path=/rsocket",
                "spring.rsocket.server.transport=websocket",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration"
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Import({JwtAuthenticationTestConfiguration.class})
class MessageNotificationServiceImplTest {

    @Autowired
    private MessageNotificationService messageNotificationService;
    @Autowired
    private JwtRSocketRequesterRepository jwtRSocketRequesterRepository;
    @LocalServerPort
    private int port;
    private static RSocketRequester requester;


    @BeforeEach
    void setUp(@Autowired RSocketRequester.Builder builder) {
        assert port != 0;
        assert jwtRSocketRequesterRepository != null;
        assert messageNotificationService != null;
        requester = builder
                .setupMetadata("real-token", AuthenticationMimeType.BEARER_TOKEN.parseMimeType())
                .websocket(URI.create("ws://localhost:" + port + "/rsocket"));
    }

    @Test
    void shouldSendMessageToRoute(@Autowired TestingMessageResponder.MessageResponder responder) throws ExecutionException, InterruptedException, TimeoutException {
        jwtRSocketRequesterRepository.save(
                Jwt.withTokenValue("real-token")
                        .subject("1")
                        .header("alg", "none")
                        .build(),
                requester
        ).block();
        messageNotificationService.sendMessage(1, new MessageNotification("test"))
                .block();
        Boolean join = responder.getFuture().get(5, TimeUnit.SECONDS);
        assertThat(join).isTrue();
    }

    @TestConfiguration
    public static class TestingMessageResponder {

        @Bean
        RSocketMessageHandlerCustomizer testMessageHandlerCustomizer() {
            return messageHandler -> messageHandler
                    .setHandlerPredicate(clazz -> clazz.isAnnotationPresent(RSocketResponder.class) || clazz.isAnnotationPresent(TestComponent.class));
        }

        @TestComponent
        private static class MessageResponder {

            private final CompletableFuture<Boolean> future = new CompletableFuture<>();

            @MessageMapping("notification.message")
            public void messageReceived(@Payload String message) {
                Gson gson = new GsonBuilder().create();
                MessageNotification messageNotification = gson.fromJson(message, MessageNotification.class);
                assertThat(messageNotification.content()).isEqualTo("test");
                future.completeAsync(() -> true);
            }

            public CompletableFuture<Boolean> getFuture() {
                return future;
            }
        }
    }
}