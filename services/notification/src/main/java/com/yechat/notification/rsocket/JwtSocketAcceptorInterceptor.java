package com.yechat.notification.rsocket;

import io.rsocket.SocketAcceptor;
import io.rsocket.plugins.SocketAcceptorInterceptor;
import org.springframework.stereotype.Component;

@Component
public class JwtSocketAcceptorInterceptor implements SocketAcceptorInterceptor {
    @Override
    public SocketAcceptor apply(SocketAcceptor socketAcceptor) {
        return new JwtSocketAcceptor(socketAcceptor);
    }
}
