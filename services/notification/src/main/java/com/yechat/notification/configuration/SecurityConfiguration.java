package com.yechat.notification.configuration;

import com.yechat.notification.rsocket.JwtPayloadInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.rsocket.authentication.AnonymousPayloadInterceptor;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;

import java.util.List;

@Configuration
public class SecurityConfiguration {

    @Bean
    public PayloadSocketAcceptorInterceptor rsocketInterceptor() {
        return new PayloadSocketAcceptorInterceptor(List.of(
                new JwtPayloadInterceptor()
        ));
    }
}
