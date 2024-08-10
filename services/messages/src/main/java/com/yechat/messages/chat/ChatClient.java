package com.yechat.messages.chat;

import com.yechat.messages.chat.response.ChatResponse;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ChatClient {

    Mono<ChatResponse> getChat(@PathVariable UUID id);
}
