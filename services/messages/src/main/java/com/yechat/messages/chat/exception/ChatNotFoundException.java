package com.yechat.messages.chat.exception;

import java.util.UUID;

public class ChatNotFoundException extends RuntimeException {
    public ChatNotFoundException(UUID chatId) {
        super(String.format("Chat not found with given id: %s", chatId.toString()));
    }
}
