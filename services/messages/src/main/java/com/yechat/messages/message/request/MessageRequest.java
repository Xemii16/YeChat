package com.yechat.messages.message.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record MessageRequest(
        @JsonProperty("chat_id") UUID chatId,
        String content
){
}
