package com.yechat.notification.configuration;

import com.yechat.notification.rsocket.jwt.JwtPrincipalArgumentResolver;
import com.yechat.notification.rsocket.jwt.JwtSocketAcceptorInterceptor;
import com.yechat.notification.rsocket.RSocketResponder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessageHandlerCustomizer;
import org.springframework.boot.rsocket.server.RSocketServerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;

@Configuration
@RequiredArgsConstructor
public class RSocketConfiguration {

    private final JwtSocketAcceptorInterceptor rsocketInterceptor;

    @Bean
    public RSocketMessageHandlerCustomizer rSocketMessageHandlerCustomizer(JwtPrincipalArgumentResolver resolver) {
        return handler -> {
            handler.setRouteMatcher(new PathPatternRouteMatcher());
            handler.setHandlerPredicate(clazz -> clazz.isAnnotationPresent(RSocketResponder.class));
            handler.getArgumentResolverConfigurer().addCustomResolver(resolver);
        };
    }

    @Bean
    public RSocketServerCustomizer rSocketServerCustomizer(RSocketMessageHandler messageHandler) {
        return server -> {
            server.acceptor(messageHandler.responder());
            server.interceptors(registry -> registry.forSocketAcceptor(rsocketInterceptor));
        };
    }
}
