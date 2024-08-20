package com.yechat.notification.rsocket.jwt;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocalJwtRSocketRequesterRepository implements JwtRSocketRequesterRepository {

    private final Map<Jwt, RSocketRequester> map = new HashMap<>();
    @Override
    public Mono<RSocketRequester> save(Jwt jwt, RSocketRequester requester) {
        return Mono.defer(() -> {
            map.put(jwt, requester);
            return Mono.just(requester);
        });
    }

    @Override
    public Mono<RSocketRequester> findByJwt(Jwt jwt) {
        return Mono.justOrEmpty(map.getOrDefault(jwt, null));
    }

    @Override
    public Mono<RSocketRequester> findBySubject(String subject) {
        return Mono.defer(() -> {
            for (Map.Entry<Jwt, RSocketRequester> entry : map.entrySet()) {
                if (entry.getKey().getSubject().equals(subject)) {
                    return Mono.just(entry.getValue());
                }
            }
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> remove(Jwt jwt) {
        return Mono.defer(() -> {
            map.remove(jwt);
            return Mono.empty();
        });
    }
}
