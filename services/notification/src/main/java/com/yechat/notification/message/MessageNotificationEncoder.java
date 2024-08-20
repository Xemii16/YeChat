package com.yechat.notification.message;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.GsonBuilder;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoder;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Deprecated
public class MessageNotificationEncoder extends AbstractEncoder<MessageNotification> {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public MessageNotificationEncoder() {
        super(MimeTypeUtils.ALL);
    }

    @Override
    public Flux<DataBuffer> encode(Publisher<? extends MessageNotification> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, MimeType mimeType, Map<String, Object> hints) {
        return Flux.from(inputStream).map(notification ->
                encodeValue(notification, bufferFactory, elementType, mimeType, hints));
    }

    @Override
    public DataBuffer encodeValue(MessageNotification value, DataBufferFactory bufferFactory, ResolvableType valueType, MimeType mimeType, Map<String, Object> hints) {
        DataBuffer dataBuffer = bufferFactory.wrap(
                gson.toJson(value)
                        .getBytes(StandardCharsets.UTF_8)
        );
        if (logger.isDebugEnabled() && !Hints.isLoggingSuppressed(hints)) {
            String logPrefix = Hints.getLogPrefix(hints);
            logger.debug(logPrefix + "Writing " + dataBuffer.readableByteCount() + " bytes");
        }
        return dataBuffer;
    }
}
