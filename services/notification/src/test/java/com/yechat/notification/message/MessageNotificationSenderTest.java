package com.yechat.notification.message;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageNotificationSenderTest {

    private static RSocketRequester requester;
    @Autowired
    private MessageNotificationSender sender;

    @BeforeAll
    public static void setupOnce(@Autowired RSocketRequester.Builder builder, @Value("${spring.rsocket.server.port}") Integer port) {
        requester = builder
                .connectTcp("localhost", port)
                .block();
    }

    @Test
    void shouldSendMessage() {
        sender.send(new MessageNotification(UUID.randomUUID(), "test", Instant.now().toEpochMilli()))
                .block();
        requester
                .route("messages")
                .retrieveMono(MessageNotification.class);
        requester
                .rsocket()
                .onClose()
                .subscribe();
    }
}