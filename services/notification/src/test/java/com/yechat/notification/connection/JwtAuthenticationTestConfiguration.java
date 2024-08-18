package com.yechat.notification.connection;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

@TestConfiguration
public class JwtAuthenticationTestConfiguration {

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return token -> {
            if (token.equals("real-token")) {
                return Mono.just(
                        Jwt.withTokenValue("real-token")
                                .subject("1")
                                .header("alg", "none")
                                .build());
            }
            if (token.equals("invalid-token")) {
                return Mono.error(new JwtException("Invalid token"));
            }
            return Mono.error(new JwtException("Invalid token"));
        };
    }
}
