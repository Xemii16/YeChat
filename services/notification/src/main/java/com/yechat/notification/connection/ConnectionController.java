package com.yechat.notification.connection;

import com.yechat.notification.message.MessageNotificationService;
import com.yechat.notification.rsocket.RSocketResponder;
import com.yechat.notification.rsocket.jwt.JwtPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.annotation.ConnectMapping;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

@RSocketResponder
@Controller
@Slf4j
@RequiredArgsConstructor
public class ConnectionController {

    private final ConnectionService connectionService;
    private final MessageNotificationService service;

    @ConnectMapping
    public Mono<Void> handleConnect(@JwtPrincipal Jwt jwt, RSocketRequester requester) {
        log.trace("Received connection request from user: {}", jwt.getSubject());
        return connectionService.connect(jwt, requester);
    }

    @MessageExceptionHandler(NullPointerException.class)
    public void handleException(NullPointerException e) {
        log.warn("Error handling connection request: {}", e.getMessage());
    }
}
