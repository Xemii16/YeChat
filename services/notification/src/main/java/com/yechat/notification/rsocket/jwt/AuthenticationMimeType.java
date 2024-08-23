package com.yechat.notification.rsocket.jwt;

import org.springframework.util.MimeType;

public enum AuthenticationMimeType {

    BEARER_TOKEN("message/x.rsocket.authentication.bearer.v0");

    private final String mimeType;

    AuthenticationMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public MimeType parseMimeType() {
        return MimeType.valueOf(mimeType);
    }

    public String getMimeTypeAsString() {
        return mimeType;
    }
}
