package com.yechat.notification.rsocket.jwt;

import io.rsocket.SocketAcceptor;
import io.rsocket.plugins.SocketAcceptorInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtSocketAcceptorInterceptor implements SocketAcceptorInterceptor {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    @Override
    public SocketAcceptor apply(SocketAcceptor socketAcceptor) {
        return new JwtSocketAcceptor(socketAcceptor, reactiveAuthenticationManager);
    }
}
