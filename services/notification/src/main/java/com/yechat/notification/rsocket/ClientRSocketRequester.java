package com.yechat.notification.rsocket;

import org.springframework.messaging.rsocket.RSocketRequester;

public record ClientRSocketRequester (
        Integer userId,
        RSocketRequester requester
){
}
