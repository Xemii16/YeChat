package com.yechat.notification.connection;

import com.yechat.notification.rsocket.RSocketResponder;
import com.yechat.notification.rsocket.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.security.oauth2.jwt.Jwt;

@RSocketResponder
@RequiredArgsConstructor
public class ConnectionController {

    @ConnectMapping
    public void handleConnect(@JwtPrincipal Jwt jwt, RSocketRequester requester) {
        assert requester != null;
        System.out.println("connect");
    }
}
