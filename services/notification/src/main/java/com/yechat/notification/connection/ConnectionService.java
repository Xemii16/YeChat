package com.yechat.notification.connection;

import com.yechat.notification.rsocket.ClientRSocketRequester;
import com.yechat.notification.rsocket.ClientRSocketRequesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ConnectionService {

    private final ClientRSocketRequesterRepository clientRSocketRequesterRepository;

    public Mono<Void> connect(String jwt, RSocketRequester requester) {
        Integer userId = Integer.parseInt(jwt);
        return clientRSocketRequesterRepository
                .save(new ClientRSocketRequester(userId, requester))
                .then();
    }
}
