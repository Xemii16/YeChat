package com.yechat.notification.user;

import com.yechat.notification.rsocket.RSocketResponder;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

@RSocketResponder
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    @MessageMapping("user.status")
    public Flux<UserStatus> findStatusesByCurrentUser(Jwt currentUser) {
        return userStatusService.findStatusesByCurrentUser(currentUser);
    }
}
