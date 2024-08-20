package com.yechat.notification.connection;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

public interface ConnectionService {

    // TODO FIXME
    /*@PreAuthorize("@verifyJwt().apply(#jwt)")*/
    Mono<Void> connect(Jwt jwt, RSocketRequester clientRequester);
}
