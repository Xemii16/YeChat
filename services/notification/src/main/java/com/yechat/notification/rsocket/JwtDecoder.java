package com.yechat.notification.rsocket;

import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractCharSequenceDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.util.MimeType;

import java.nio.charset.Charset;
import java.util.List;

public class JwtDecoder extends AbstractCharSequenceDecoder<String> {

    protected JwtDecoder(List<String> delimiters, boolean stripDelimiter, MimeType... mimeTypes) {
        super(delimiters, stripDelimiter, mimeTypes);
    }

    @Override
    public boolean canDecode(ResolvableType elementType, MimeType mimeType) {
        return (elementType.resolve() == String.class && super.canDecode(elementType, mimeType));
    }

    @Override
    protected String decodeInternal(DataBuffer dataBuffer, Charset charset) {
        return dataBuffer.toString(charset);
    }

    public static JwtDecoder textJwt() {
        return new JwtDecoder(DEFAULT_DELIMITERS, true,
                new MimeType("application", "jwt", DEFAULT_CHARSET));
    }


}
