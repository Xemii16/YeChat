package com.yechat.notification.rsocket;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ClientRSocketRequesterRepository {

    private final Map<Integer, ClientRSocketRequester> connections = new ConcurrentHashMap<>();

    public <S extends ClientRSocketRequester> Mono<S> save(S entity) {
        if (!connections.containsKey(entity.userId())) {
            connections.put(entity.userId(), entity);
            return Mono.just(entity);
        }
        connections.get(entity.userId()).requester().rsocket().dispose();
        connections.remove(entity.userId());
        connections.put(entity.userId(), entity);
        return Mono.just(entity);
    }
    public Mono<ClientRSocketRequester> findById(Integer userId) {
        return Mono.justOrEmpty(Optional.ofNullable(connections.getOrDefault(userId, null)));
    }
}
