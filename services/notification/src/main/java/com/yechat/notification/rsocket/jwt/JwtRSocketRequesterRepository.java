package com.yechat.notification.rsocket.jwt;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

public interface JwtRSocketRequesterRepository {

    Mono<RSocketRequester> save(Jwt jwt, RSocketRequester requester);

    Mono<RSocketRequester> findByJwt(Jwt jwt);

    Mono<RSocketRequester> findBySubject(String subject);

    Mono<Void> remove(Jwt jwt);
}
