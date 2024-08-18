package com.yechat.notification.rsocket;

import io.netty.buffer.ByteBuf;
import io.rsocket.ConnectionSetupPayload;
import io.rsocket.RSocket;
import io.rsocket.SocketAcceptor;
import io.rsocket.metadata.CompositeMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.rsocket.metadata.BearerTokenMetadata;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
public class JwtSocketAcceptor implements SocketAcceptor {

    private final SocketAcceptor delegate;
    private final ReactiveAuthenticationManager authenticationManager;

    public JwtSocketAcceptor(SocketAcceptor delegate, ReactiveAuthenticationManager authenticationManager) {
        this.delegate = delegate;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Mono<RSocket> accept(ConnectionSetupPayload setup, RSocket sendingSocket) {
        // TODO add authentication and writing to the context
        ByteBuf byteBuf = setup.metadata();
        CompositeMetadata compositeMetadata = new CompositeMetadata(byteBuf, false);
        Optional<BearerTokenAuthenticationToken> token = compositeMetadata.stream()
                .filter(entry -> BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE.toString().equals(entry.getMimeType()))
                .map(entry -> {
                    ByteBuf content = entry.getContent();
                    String tokenString = content.toString(StandardCharsets.UTF_8);
                    return new BearerTokenAuthenticationToken(tokenString);
                })
                .findFirst();
        return token
                .map(authenticationToken -> authenticationManager.authenticate(authenticationToken)
                        .flatMap(authentication -> delegate.accept(setup, sendingSocket)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))))
                .orElseGet(() -> delegate.accept(setup, sendingSocket));
    }
}
