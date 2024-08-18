package com.yechat.notification.configuration;

import com.yechat.notification.rsocket.JwtPayloadInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.rsocket.authentication.AnonymousPayloadInterceptor;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

import java.util.List;

@Configuration
public class SecurityConfiguration {

    @Bean
    public PayloadSocketAcceptorInterceptor rsocketInterceptor(RSocketSecurity security) {
        security
                .authorizePayload(authorize -> authorize
                        .anyRequest().permitAll()
                        .anyExchange().permitAll()
                )
                .jwt(Customizer.withDefaults());
        return security.build();
    }

    @Bean
    ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveJwtDecoder decoder) {
        return new JwtReactiveAuthenticationManager(decoder);
    }
}
