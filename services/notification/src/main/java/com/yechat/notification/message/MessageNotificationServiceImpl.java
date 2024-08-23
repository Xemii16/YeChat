package com.yechat.notification.message;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import com.yechat.notification.rsocket.jwt.JwtRSocketRequesterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("!notification-listener-test")
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
                        .data(gson.toJson(message)) // will be replaced with custom encoder
                        .send()
                )
                .doOnError(e -> log.debug("Failed sending message notification (userId: {})", userId, e))
                .doOnSuccess(v -> log.debug("Sent message notification (userId: {})", userId));
    }

    @Override
    public Mono<Void> sendMessage(MessageInformation messageInformation) {
        return sendMessage(messageInformation.receiverId(), messageInformation.messageNotification());
    }
}
