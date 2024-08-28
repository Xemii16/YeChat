package com.yechat.notification.chat;

import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

public interface ChatClient {

    Flux<ChatResponse> getChats(Jwt jwt);
}
