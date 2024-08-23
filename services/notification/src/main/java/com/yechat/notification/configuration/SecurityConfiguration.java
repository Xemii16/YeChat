package com.yechat.notification.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.function.Function;

@Configuration
@EnableReactiveMethodSecurity
public class SecurityConfiguration {

    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveJwtDecoder decoder) {
        return new JwtReactiveAuthenticationManager(decoder);
    }

    @Bean
    public Function<Jwt, Mono<Boolean>> verifyJwt() {
        return jwt -> Mono.defer(() -> {
            Instant notBefore = jwt.getNotBefore();
            Instant expiresAt = jwt.getExpiresAt();
            if (expiresAt == null) {
                return Mono.just(false);
            }
            if (notBefore.isAfter(Instant.now())) {
                return Mono.just(false);
            }
            if (expiresAt.isBefore(Instant.now())) {
                return Mono.just(false);
            }
            return Mono.just(true);
        });
    }
}
