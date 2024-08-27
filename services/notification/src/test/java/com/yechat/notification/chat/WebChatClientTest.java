package com.yechat.notification.chat;

import com.netflix.discovery.EurekaClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.EnableTestBinder;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.io.File;
import java.time.Duration;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest
@Import(WebChatClient.class)
@AutoConfigureWebClient
@EnableTestBinder
@Testcontainers
class WebChatClientTest {

    @Container
    public static DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("src/test/resources/chat-test-compose.yml"));

    @Autowired
    private WebChatClient webChatClient;

    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @BeforeEach
    void setUp() {
        await().atMost(Duration.ofSeconds(60)).until(() -> eurekaClient.getApplications().size() > 0);
        assert webChatClient != null;
    }

    @Test
    void getChatsSuccessfully() {
        StepVerifier
                .create(webChatClient.getChats(Jwt
                        .withTokenValue("authenticated-token") // use profile "development" for fake JWT token
                        .subject("1")
                        .header("alg", "none")
                        .build()
                )).expectNextCount(0)
                .verifyComplete();
    }
}