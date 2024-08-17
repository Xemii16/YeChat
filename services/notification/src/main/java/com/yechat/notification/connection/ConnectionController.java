package com.yechat.notification.connection;

import com.yechat.notification.rsocket.RSocketResponder;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

@RSocketResponder
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;

    @ConnectMapping
    public Mono<Void> connect(@AuthenticationPrincipal Jwt jwt, RSocketRequester requester) {
        System.out.println(jwt.getClaimAsString("sub"));
        return Mono.empty();
    }
}
