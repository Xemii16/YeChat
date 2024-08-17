package com.yechat.notification.configuration;

import com.yechat.notification.rsocket.JwtPayloadInterceptor;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessageHandlerCustomizer;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.boot.rsocket.server.RSocketServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.rsocket.EnableRSocketSecurity;
import org.springframework.security.config.annotation.rsocket.RSocketSecurity;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager;
import org.springframework.security.rsocket.authentication.AuthenticationPayloadInterceptor;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.security.rsocket.core.SecuritySocketAcceptorInterceptor;

@Configuration
@EnableRSocketSecurity
public class SecurityConfiguration {

    @Bean
    public PayloadSocketAcceptorInterceptor rsocketInterceptor(
            RSocketSecurity rsocket,
            JwtPayloadInterceptor jwtPayloadInterceptor
    ) {
        rsocket
                .authorizePayload(authorize ->
                        authorize
                                .anyRequest().permitAll()
                                .anyExchange().permitAll()
                )
                .addPayloadInterceptor(jwtPayloadInterceptor);
        return rsocket.build();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveJwtDecoder jwtDecoder) {
        return new JwtReactiveAuthenticationManager(jwtDecoder);
    }

    @Bean
    public AuthenticationPayloadInterceptor authenticationPayloadInterceptor(ReactiveAuthenticationManager authenticationManager) {
        return new AuthenticationPayloadInterceptor(authenticationManager);
    }
}
