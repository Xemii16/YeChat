package com.yechat.notification.chat;

import com.yechat.notification.exception.ServiceNotAvailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class WebChatClient implements ChatClient {

    private final WebClient webClient;

    public WebChatClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://chat-service")
                .build();
    }

    @Override
    public Flux<ChatResponse> getChats(Jwt jwt) {
        return webClient
                .get().uri("/api/v1/chats")
                .headers(headers -> headers.setBearerAuth(jwt.getTokenValue()))
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.warn("Error from user-service: {}", clientResponse);
                    return Mono.error(new ServiceNotAvailableException("chat-service"));
                })
                .bodyToFlux(ChatResponse.class);
    }
}
