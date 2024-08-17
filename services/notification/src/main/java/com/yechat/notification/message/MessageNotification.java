package com.yechat.notification.message;

import java.util.UUID;

public record MessageNotification(
        UUID chatId,
        String content,
        Long timestamp
) {
}
