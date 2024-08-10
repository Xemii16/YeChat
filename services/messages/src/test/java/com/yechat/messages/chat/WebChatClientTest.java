package com.yechat.messages.chat;

import com.netflix.discovery.EurekaClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraReactiveRepositoriesAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest()
@EnableAutoConfiguration(exclude = {
        CassandraAutoConfiguration.class,
        CassandraReactiveRepositoriesAutoConfiguration.class,
        CassandraReactiveDataAutoConfiguration.class,
})
@Import(WebChatClient.class)
@AutoConfigureWebClient
@ActiveProfiles("testing")
@Testcontainers
class WebChatClientTest {

    @Container
    public static DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("src/test/resources/compose-test.yml"));

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("eureka.client.serviceUrl.defaultZone",
                () -> "http://localhost:"
                        + 8761
                        + "/eureka/");
    }

    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @BeforeEach
    void setUp() {
        await().atMost(Duration.ofSeconds(30)).until(() -> eurekaClient.getApplications().size() > 0);
    }

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private WebChatClient webChatClient;

    @Test
    @Disabled
    // TODO will be implemented
    void getChat() {
        /*this.webClientBuilder.build()
                .post()
        this.webChatClient.getChat().block();*/
    }
}