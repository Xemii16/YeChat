package com.yechat.messages.chat.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record ChatResponse(
        @JsonProperty("chat_id")
        UUID chatId,
        @JsonProperty("receiver_id")
        Integer receiverId
) {
}
