package com.yechat.messages.chat.exception;

import com.yechat.messages.chat.ChatClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ChatClientFallbackFactory implements FallbackFactory<ChatClient> {

    @Override
    public ChatClient create(Throwable cause) {
        return id -> Optional.empty();
    }
}
