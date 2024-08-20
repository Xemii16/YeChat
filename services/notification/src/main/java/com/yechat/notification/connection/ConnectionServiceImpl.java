package com.yechat.notification.connection;

import com.yechat.notification.rsocket.jwt.JwtRSocketRequesterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {

    private final JwtRSocketRequesterRepository jwtRSocketRequesterRepository;

    @Override
    public Mono<Void> connect(Jwt jwt, RSocketRequester clientRequester) {
        if (clientRequester == null) {
            return Mono.error(new NullPointerException("RSocketRequester is null"));
        }
        return jwtRSocketRequesterRepository.save(jwt, clientRequester)
                .doOnSuccess(v -> log.debug("Saved connected user with id: {}", jwt.getSubject()))
                .then();
    }
}
