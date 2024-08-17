package com.yechat.notification.connection;

import com.yechat.notification.message.MessageNotificationSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.rsocket.metadata.BearerTokenMetadata;
import org.springframework.util.MimeType;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(
        properties = {
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration"
        }
)
class ConnectionControllerTest {

    private static RSocketRequester requester;
    @Autowired
    private MessageNotificationSender sender;

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @BeforeAll
    public static void setupOnce(@Autowired RSocketRequester.Builder builder, @Value("${spring.rsocket.server.port}") Integer port) {
        requester = builder
                .setupMetadata("token", BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .tcp("localhost", port);
    }

    @Test
    void shouldConnect() {
        when(jwtDecoder.decode(anyString()))
                .thenAnswer(invocation -> Mono.just(Jwt.withTokenValue("token").subject("1").build()));
        StepVerifier
                .create(requester.rsocketClient().source())
                .consumeNextWith(client -> {
                    assertThat(client.availability()).isEqualTo(1.0);
                })
                .verifyComplete();
    }
}