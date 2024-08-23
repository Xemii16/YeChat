package com.yechat.notification.message;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.converter.AbstractMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class MessageInformationConverter extends AbstractMessageConverter {

    public MessageInformationConverter() {
        super(MimeTypeUtils.APPLICATION_JSON);
    }

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @Override
    protected boolean supports(@NonNull Class<?> clazz) {
        return MessageInformation.class.equals(clazz);
    }

    @Override
    protected Object convertFromInternal(Message<?> message, @NonNull Class<?> targetClass, Object conversionHint) {
        Object payload = message.getPayload();
        MessageHeaders headers = message.getHeaders();
        if (!headers.containsKey(KafkaHeaders.RECEIVED_KEY)) {
            log.warn("Key {} not found in message headers", KafkaHeaders.RECEIVED_KEY);
            return null;
        }
        if (payload instanceof byte[]) {
            MessageNotification notification = gson.fromJson(
                    new String((byte[]) message.getPayload(), StandardCharsets.UTF_8),
                    MessageNotification.class
            );
            int userId = Integer.parseInt(new String((byte[]) headers.get(KafkaHeaders.RECEIVED_KEY)));
            return new MessageInformation(
                    userId,
                    notification
            );
        }
        return super.convertFromInternal(message, targetClass, conversionHint);
    }
}
