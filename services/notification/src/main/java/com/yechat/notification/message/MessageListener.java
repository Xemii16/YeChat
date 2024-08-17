package com.yechat.notification.message;

/*import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverOffset;*/

import java.util.function.Consumer;

/*@Service
@Slf4j
@RequiredArgsConstructor*/
public class MessageListener {

    /*private final ObjectMapper objectMapper;
    @Value("${kafka.topic.message}")
    private static String MESSAGE_TOPIC;
    private final MessageNotificationSender sender;

    @Bean
    public Consumer<Flux<Message<MessageNotification>>> consumeMessage() {
        return stream -> stream
                .doOnNext(this::acceptMessage)
                .doOnError(MessageListener::onError)
                .subscribe();
    }

    private void acceptMessage(Message<MessageNotification> message) {
        if (message == null) {
            log.error("Received message from {} is null (Apache Kafka)", MESSAGE_TOPIC);
            return;
        }
        sender.send(message.getPayload())
                .subscribe();
        message.getHeaders()
                .get(KafkaHeaders.ACKNOWLEDGMENT, ReceiverOffset.class)
                .acknowledge();
    }

    private static void onError(Throwable throwable) {
        log.error("Error occurred while consuming message from {} (Apache Kafka)", MESSAGE_TOPIC, throwable);
    }*/
}

