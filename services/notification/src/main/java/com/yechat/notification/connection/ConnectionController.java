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

    @MessageMapping("test")
    public Mono<Void> test() {
        return Mono.empty();
    }

    @ConnectMapping
    public void connect(RSocketRequester requester) {
        assert requester != null;
        System.out.println("connect");
    }
}
