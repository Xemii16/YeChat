package com.yechat.notification.message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverOffset;

import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageNotificationListener {

    private final MessageNotificationService messageNotificationService;

    @Bean
    public Consumer<Flux<Message<MessageInformation>>> listenMessageInformation() {
        return flux -> flux
                .doOnNext(informationMessage -> log.trace("Received message: {}", informationMessage))
                .subscribe(informationMessage -> messageNotificationService.sendMessage(informationMessage.getPayload())
                        .subscribe(e -> {
                            log.trace("Message sent: {}", informationMessage);
                            offsetAcknowledge(informationMessage);
                            log.info("Message sent to user: {}", informationMessage.getPayload().receiverId());
                        }, e -> {
                            log.debug("Failed sending message", e);
                            offsetAcknowledge(informationMessage);
                        }));
    }

    private static void offsetAcknowledge(Message<?> message) {
        ReceiverOffset receiverOffset = message.getHeaders()
                .get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class);
        if (receiverOffset != null) {
            receiverOffset.acknowledge();
            log.trace("Message acknowledged: {}", message);
        }
    }
}
