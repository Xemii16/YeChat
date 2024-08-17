package com.yechat.testing;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.rsocket.metadata.BearerTokenMetadata;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
class TestingApplicationTests {

    @MockBean
    private ReactiveJwtDecoder jwtDecoder;

    @Test
    void connect() {
        when(jwtDecoder.decode(anyString()))
                .thenAnswer(invocation -> Mono.just(Jwt.withTokenValue("token").subject("1").build()));
        RSocketRequester tcp = RSocketRequester.builder()
                .setupMetadata("token", BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE)
                .tcp("localhost", 7000);
        tcp.rsocketClient().connect();
    }

}
