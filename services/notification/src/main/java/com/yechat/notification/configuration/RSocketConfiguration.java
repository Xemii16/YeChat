package com.yechat.notification.configuration;

import com.yechat.notification.rsocket.RSocketResponder;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessageHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

@Configuration
public class RSocketConfiguration {

    @Bean
    public RSocketMessageHandlerCustomizer rSocketMessageHandlerCustomizer() {
        return handler -> {
            handler.setRouteMatcher(new PathPatternRouteMatcher());
            handler.setHandlerPredicate(clazz -> clazz.isAnnotationPresent(RSocketResponder.class));
        };
    }
}
