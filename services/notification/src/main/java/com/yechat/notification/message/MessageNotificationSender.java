package com.yechat.notification.message;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MessageNotificationSender {

    private final RSocketRequester requester;

    public Mono<Void> send(MessageNotification message) {
        return requester.route("messages")
                .data(message)
                .send();
    }
}
