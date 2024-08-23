package com.yechat.notification.message;

public record MessageNotification(
        Integer senderId,
        String content
) {
}
