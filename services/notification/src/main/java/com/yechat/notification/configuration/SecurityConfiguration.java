package com.yechat.notification.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import reactor.core.publisher.Mono;

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
        // TODO: implement this
        return jwt -> Mono.just(true);
    }
}
