package com.yechat.notification.configuration;

import com.yechat.notification.connection.ConnectionController;
import com.yechat.notification.connection.ConnectionService;
import com.yechat.notification.rsocket.JwtDecoder;
import com.yechat.notification.rsocket.RSocketResponder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.rsocket.RSocketMessageHandlerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.RSocketStrategies;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.messaging.handler.invocation.reactive.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.rsocket.core.SecuritySocketAcceptorInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.util.pattern.PathPatternRouteMatcher;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;

@Configuration
public class RSocketServerConfiguration {

    @Bean
    public RSocketMessageHandler rsocketMessageHandler() {
        RSocketMessageHandler handler = new RSocketMessageHandler();
        handler.setRouteMatcher(new PathPatternRouteMatcher());
        handler.setHandlerPredicate(clazz -> AnnotationUtils
                .isCandidateClass(clazz, RSocketResponder.class)
        );
        handler.getArgumentResolverConfigurer()
                .addCustomResolver(new AuthenticationPrincipalArgumentResolver());
        return handler;
    }

    @Bean
    public RSocketRequester getRSocketRequester(
            @Value("${spring.rsocket.server.port}") Integer port,
            RSocketMessageHandler handler,
            SecuritySocketAcceptorInterceptor securitySocketAcceptorInterceptor
    ) {
        RSocketRequester.Builder builder = RSocketRequester.builder();
        return builder
                .rsocketConnector(rSocketConnector -> rSocketConnector
                        .reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2)))
                        .acceptor(securitySocketAcceptorInterceptor.apply(handler.responder()))
                )
                .tcp("localhost", port);
    }
}
