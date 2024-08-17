package com.yechat.notification.rsocket;

import io.netty.buffer.ByteBuf;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.metadata.CompositeMetadata;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class JwtSocketAcceptor implements SocketAcceptor {

    private final SocketAcceptor delegate;

    public JwtSocketAcceptor(SocketAcceptor delegate) {
        this.delegate = delegate;
    }

    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
        return delegate.accept(setup, sendingSocket);
    }
}
