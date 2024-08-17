package com.yechat.notification.configuration;

import com.yechat.notification.rsocket.JwtSocketAcceptorInterceptor;
import io.rsocket.core.RSocketServer;
import io.rsocket.transport.netty.server.WebsocketServerTransport;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.messaging.rsocket.annotation.support.RSocketMessageHandler;
import org.springframework.security.rsocket.core.PayloadSocketAcceptorInterceptor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RSocketServerRunner implements ApplicationRunner {

    private final RSocketMessageHandler messageHandler;
    private final JwtSocketAcceptorInterceptor rsocketInterceptor;
    private final PayloadSocketAcceptorInterceptor payloadSocketAcceptorInterceptor;

    @Override
    public void run(ApplicationArguments args) {
        RSocketServer
                .create(messageHandler.responder())
                .interceptors(registry -> registry.forSocketAcceptor(rsocketInterceptor))
                .bind(WebsocketServerTransport.create(8080))
                .block();
    }
}
