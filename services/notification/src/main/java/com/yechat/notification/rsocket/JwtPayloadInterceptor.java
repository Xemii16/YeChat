package com.yechat.notification.rsocket;

import io.netty.buffer.ByteBuf;
import io.rsocket.metadata.CompositeMetadata;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthenticationToken;
import org.springframework.security.rsocket.api.PayloadExchange;
import org.springframework.security.rsocket.api.PayloadInterceptor;
import org.springframework.security.rsocket.api.PayloadInterceptorChain;
import org.springframework.security.rsocket.metadata.BearerTokenMetadata;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class JwtPayloadInterceptor implements PayloadInterceptor {

    private static final String BEARER_MIME_TYPE_VALUE = BearerTokenMetadata.BEARER_AUTHENTICATION_MIME_TYPE.toString();

    @Override
    public Mono<Void> intercept(PayloadExchange exchange, PayloadInterceptorChain chain) {
        return ReactiveSecurityContextHolder.getContext().switchIfEmpty(Mono.defer(() -> {
            ByteBuf metadata = exchange.getPayload().metadata();
            CompositeMetadata compositeMetadata = new CompositeMetadata(metadata, false);
            Optional<BearerTokenAuthenticationToken> bearerToken = compositeMetadata.stream()
                    .filter(entry -> BEARER_MIME_TYPE_VALUE.equals(entry.getMimeType()))
                    .map(entry -> {
                        ByteBuf content = entry.getContent();
                        String token = content.toString(StandardCharsets.UTF_8);
                        return new BearerTokenAuthenticationToken(token);
                    })
                    .findFirst();
            Mono<Void> next = chain.next(exchange);
            bearerToken
                    .ifPresent(token -> next.contextWrite(ReactiveSecurityContextHolder.withAuthentication(token)));
            return next
                    .then(Mono.empty());
        })).flatMap((securityContext) -> chain.next(exchange));
    }
}
