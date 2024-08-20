package com.yechat.notification.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.yechat.notification.rsocket.jwt.JwtRSocketRequesterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageNotificationServiceImpl implements MessageNotificationService {

    private final JwtRSocketRequesterRepository jwtRSocketRequesterRepository;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    public Mono<Void> sendMessage(Integer userId, MessageNotification message) {
        return jwtRSocketRequesterRepository.findBySubject(userId.toString())
                .switchIfEmpty(Mono.error(new RSocketRequesterNotFound("Connected requester not found for user with id: " + userId)))
                .flatMap(requester -> requester.route("notification.message")
                        .data(gson.toJson(message))
                        .send())
                .doOnError(e -> log.error("Error sending message notification to user with id: {}", userId, e))
                .doOnSuccess(v -> log.debug("Sent message notification to user with id: {}", userId));
    }
}
