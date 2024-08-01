package com.yechat.messages.chat;

import com.yechat.messages.chat.exception.ChatClientFallbackFactory;
import com.yechat.messages.chat.response.ChatResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name = "chat-service", fallbackFactory = ChatClientFallbackFactory.class)
public interface ChatClient {

    // Note: This method is not implemented in the chat service
    @GetMapping("/api/v1/chats/{id}")
    Optional<ChatResponse> getChat(@PathVariable UUID id);
}
