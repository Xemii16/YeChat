package com.yechat.chats.user;

import com.netflix.discovery.EurekaClient;
import com.yechat.chats.user.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest()
@EnableAutoConfiguration(exclude = {
        R2dbcAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        FlywayAutoConfiguration.class
})
@AutoConfigureWebClient
@ActiveProfiles("testing")
@Testcontainers
class WebUserClientTest {

    @Container
    public static DockerComposeContainer<?> environment =
            new DockerComposeContainer<>(new File("src/test/resources/user-client-compose.yml"));

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("eureka.client.serviceUrl.defaultZone",
                () -> "http://discovery-server:"
                        + 8761
                        + "/eureka/");
    }

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @BeforeEach
    void setUp() {
        await().atMost(Duration.ofSeconds(30)).until(() -> eurekaClient.getApplications().size() > 0);
    }

    @Autowired
    private UserClient userClient;


    @Test
    void getUser() {
        UserResponse registeredUserResponse = this.webClientBuilder.build()
                .post()
                .uri("http://localhost:8080/api/v1/users")
                .bodyValue(new UserRequest("test@test.com", "test", "test", "test"))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .block();
        assert registeredUserResponse != null;
        UserResponse response = this.userClient
                .getUser(registeredUserResponse.id())
                .block();
        assertThat(registeredUserResponse).isEqualTo(response);
    }


    private record UserRequest(
            String email,
            String firstname,
            String lastname,
            String username
    ) {
    }

}