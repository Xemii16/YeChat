package com.yechat.notification.connection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.rsocket.RSocketRequester;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class ConnectionControllerTest {

    private static RSocketRequester requester;

    @BeforeAll
    static void beforeAll(@Autowired RSocketRequester.Builder builder) {
        requester = builder
                .websocket(URI.create("ws://localhost:8080/rsocket"));
    }

    @Test
    void test1() {
        StepVerifier.create(requester.route("test")
                .data(Mono.empty())
                .send())
                .verifyComplete();
    }
}