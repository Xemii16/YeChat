package com.yechat.notification.connection;

import io.rsocket.core.RSocketServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessagingAutoConfiguration;
import org.springframework.boot.autoconfigure.rsocket.RSocketServerAutoConfiguration;
import org.springframework.boot.rsocket.context.RSocketServerBootstrap;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.rsocket.metadata.BearerTokenMetadata;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @BeforeAll
    static void beforeAll(@Autowired RSocketRequester.Builder builder) {
       /* requester = builder
                .setupMetadata("real-token", BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .websocket(URI.create("ws://localhost:" + port + "/rsocket"));*/
    }

    @BeforeEach
    void setUp(@Autowired RSocketRequester.Builder builder) {
        requester = builder
                .setupMetadata("real-token", BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .websocket(URI.create("ws://localhost:" + port + "/rsocket"));
    }

    @Test
    void test1() {
        StepVerifier.create(requester.route("test")
                        .data(Mono.empty())
                        .send())
                .verifyComplete();
    }
}