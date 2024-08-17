package com.yechat.notification.rsocket;

import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.rsocket.api.PayloadExchange;
import org.springframework.security.rsocket.api.PayloadInterceptor;
import org.springframework.security.rsocket.api.PayloadInterceptorChain;
import org.springframework.security.rsocket.authentication.BearerPayloadExchangeConverter;
import org.springframework.security.rsocket.authentication.PayloadExchangeAuthenticationConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Order(5)
public class JwtPayloadInterceptor implements PayloadInterceptor {

    private final ReactiveAuthenticationManager authenticationManager;
    private final PayloadExchangeAuthenticationConverter authenticationConverter =
            new BearerPayloadExchangeConverter();

    public JwtPayloadInterceptor(ReactiveAuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<Void> intercept(PayloadExchange exchange, PayloadInterceptorChain chain) {
        return this.authenticationConverter.convert(exchange)
                .switchIfEmpty(chain.next(exchange).then(Mono.empty()))
                .flatMap(this.authenticationManager::authenticate)
                .flatMap((a) -> onAuthenticationSuccess(chain.next(exchange), a));
    }

    private Mono<Void> onAuthenticationSuccess(Mono<Void> payload, Authentication authentication) {
        return payload.contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
    }
}
