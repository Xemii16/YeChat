package com.yechat.notification.chat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ChatResponse(
        @JsonProperty("chat_id")
        UUID chatId,
        @JsonProperty("receiver_id")
        Integer receiverId
) {
}
