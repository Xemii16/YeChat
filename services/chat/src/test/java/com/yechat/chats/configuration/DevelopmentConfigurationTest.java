package com.yechat.chats.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
@ActiveProfiles("development")
class DevelopmentConfigurationTest {

    @Autowired
    private ReactiveJwtDecoder jwtDecoder;

    @Test
    void assertThatJwtDecoderReturnFakeTokenSuccessfully() {
        Jwt jwt = jwtDecoder.decode("authenticated-token").block();
        assert jwt != null;
        assertThat(jwt.getSubject()).isEqualTo("1");
    }

    @Test
    void assertThatJwtDecoderReturnException() {
        StepVerifier
                .create(jwtDecoder.decode("invalid-token"))
                .expectError(BadJwtException.class)
                .verify();
    }
}