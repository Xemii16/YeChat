package com.yechat.messages.chat;

import com.yechat.messages.chat.exception.ChatNotFoundException;
import com.yechat.messages.chat.response.ChatResponse;
import com.yechat.messages.exception.ServiceNotAvailableException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@Slf4j
public class WebChatClient implements ChatClient {

    private final WebClient webClient;

    public WebChatClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://user-service")
                .build();
    }

    @Override
    public Mono<ChatResponse> getChat(UUID id) {
        return webClient
                .get().uri("/api/v1/users/" + id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new ChatNotFoundException(id)))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse -> {
                    log.warn("Error from user-service: {}", clientResponse);
                    return Mono.error(new ServiceNotAvailableException("user-service"));
                })
                .bodyToMono(ChatResponse.class);
    }


}
