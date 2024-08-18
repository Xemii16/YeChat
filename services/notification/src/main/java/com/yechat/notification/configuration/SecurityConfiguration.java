package com.yechat.notification.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;

@Configuration
public class SecurityConfiguration {

    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveJwtDecoder decoder) {
        return new JwtReactiveAuthenticationManager(decoder);
    }
}
