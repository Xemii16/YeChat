package com.yechat.notification.message;

import reactor.core.publisher.Mono;

public interface MessageNotificationService {

    Mono<Void> sendMessage(Integer userId, MessageNotification message);
}
