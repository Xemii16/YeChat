package com.yechat.chats.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import reactor.core.publisher.Mono;

@Configuration
@Profile("development")
@PropertySource("classpath:application-development.yml")
public class DevelopmentConfiguration {

    @Bean
    ReactiveJwtDecoder jwtDecoder() {
        return token -> {
            if (token.equals("authenticated-token")) {
                return Mono.just(
                        Jwt.withTokenValue("authenticated-token")
                                .header("alg", "none")
                                .subject("1")
                                .build()
                );
            }
            return Mono.error(new BadJwtException("Invalid token"));
        };
    }
}
