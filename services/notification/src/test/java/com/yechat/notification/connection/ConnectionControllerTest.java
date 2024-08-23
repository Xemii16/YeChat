package com.yechat.notification.connection;

import com.yechat.notification.JwtAuthenticationTestConfiguration;
import com.yechat.notification.rsocket.jwt.AuthenticationMimeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;
import java.time.Duration;

@SpringBootTest(
        properties = {
                "spring.rsocket.server.mapping-path=/rsocket",
                "spring.rsocket.server.transport=websocket",
                "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.oauth2.resource.reactive.ReactiveOAuth2ResourceServerAutoConfiguration"
        },
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Import(JwtAuthenticationTestConfiguration.class)
class ConnectionControllerTest {

    private static RSocketRequester requester;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp(@Autowired RSocketRequester.Builder builder) {
        requester = builder
                .setupMetadata("real-token", AuthenticationMimeType.BEARER_TOKEN.parseMimeType())
                .websocket(URI.create("ws://localhost:" + port + "/rsocket"));
        requester.rsocketClient().source().block(Duration.ofSeconds(5));
    }

    @Test
    void test1() {
        StepVerifier.create(requester.route("test")
                        .data(Mono.empty())
                        .send())
                .verifyComplete();
    }
}