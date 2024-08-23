package com.yechat.notification.message;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.publisher.PublisherProbe;

import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("notification-listener-test")
@EnableTestBinder
class MessageNotificationListenerTest {

    @Autowired
    private InputDestination input;

    @MockBean
    private MessageNotificationServiceImpl service;
    private final PublisherProbe<Void> probe = PublisherProbe.empty();
    private final MessageInformation messageInformation = new MessageInformation(1, new MessageNotification(2, "test"));

    @BeforeEach
    void setUp() {
        when(service.sendMessage(messageInformation)).thenReturn(probe.mono());
    }

    @Test
    void shouldReceiveMessage() {
        input.send(new GenericMessage<>(messageInformation));
        probe.assertWasSubscribed();
    }
}