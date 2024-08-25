package com.yechat.chats.chat;

import com.yechat.chats.chat.exception.ChatExceptionHandler;
import com.yechat.chats.chat.request.ChatRequest;
import com.yechat.chats.chat.response.ChatResponse;
import com.yechat.chats.user.UserClient;
import com.yechat.chats.user.exception.UserExceptionHandler;
import com.yechat.chats.user.exception.UserNotFoundException;
import com.yechat.chats.user.response.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.AutoConfigureDataR2dbc;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(ChatController.class)
@Import({ChatMapper.class, ChatService.class, ChatExceptionHandler.class, UserExceptionHandler.class})
@AutoConfigureDataR2dbc
@AutoConfigureWebTestClient
@Testcontainers
class ChatControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private UserClient userClient;
    @Autowired
    private ChatRepository chatRepository;

    @Container
    final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("chats")
            .withUsername("user")
            .withPassword("password");

    @DynamicPropertySource
    static void registerPostgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> postgres.getJdbcUrl()
                .replace("jdbc", "r2dbc"));
        registry.add("spring.r2dbc.username", postgres::getUsername);

        registry.add("spring.r2dbc.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @BeforeEach
    void setUp() {
        when(userClient.getUser(2))
                .thenReturn(Mono.just(new UserResponse(2, "test", "test", "test")));
        when(userClient.getUser(3))
                .thenReturn(Mono.error(new UserNotFoundException(3)));
    }

    @AfterEach
    void tearDown() {
        chatRepository.deleteAll()
                .block();
    }

    @Test
    void shouldCreateChatSuccessfully() {
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ChatRequest(2))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.chat_id").isNotEmpty()
                .jsonPath("$.receiver_id").isEqualTo(2);
    }

    @Test
    void shouldReturnBadRequestWhenReceiverNotFound() {
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ChatRequest(3))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnUnauthorizedWhenUserNotAuthenticated() {
        this.webTestClient
                .mutateWith(csrf())
                .post().uri("/api/v1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ChatRequest(2))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturnBadRequestWhenChatExists() {
        ChatRequest body = new ChatRequest(2);
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ChatRequest(2))
                .exchange();
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnAllChats() {
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ChatRequest(2))
                .exchange();
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .get().uri("/api/v1/chats")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$[0].chat_id").isNotEmpty()
                .jsonPath("$[0].receiver_id").isEqualTo(2)
                .jsonPath("$[1]").doesNotExist();
    }

    @Test
    void shouldReturnAuthorizedWhenUserNotAuthenticated() {
        this.webTestClient
                .mutateWith(csrf())
                .get().uri("/api/v1/chats")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturnEmptyListWhenNoChats() {
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .get().uri("/api/v1/chats")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$").isEmpty();
    }

    @Test
    void shouldDeleteChatForSender() {
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ChatRequest(2))
                .exchange();
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .delete().uri("/api/v1/chats/" + 2)
                .exchange()
                .expectStatus().isNoContent();
        assertThat(chatRepository.findAllBySenderId(1).count().block()).isEqualTo(0);
    }

    @Test
    void shouldReturnBadRequestWhenChatNotExists() {
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .delete().uri("/api/v1/chats/" + 2)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void shouldReturnUnauthorizedWhenDeleteUserAndNotAuthenticated() {
        this.webTestClient
                .mutateWith(csrf())
                .delete().uri("/api/v1/chats/" + 2)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldDeleteChatForBothUsers() {
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ChatRequest(2))
                .exchange();
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .delete().uri("/api/v1/chats/2?all=true")
                .exchange()
                .expectStatus().isNoContent();
        assertThat(chatRepository.findAllBySenderId(1).count().block()).isEqualTo(0);
        assertThat(chatRepository.findAllBySenderId(2).count().block()).isEqualTo(0);
    }

    @Test
    void shouldReturnChatById() {
        ChatResponse response = this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ChatRequest(2))
                .exchange()
                .expectBody(ChatResponse.class)
                .returnResult().getResponseBody();
        assert response != null;
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .get().uri("/api/v1/chats/" + response.getChatId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.chat_id").isEqualTo(response.getChatId().toString())
                .jsonPath("$.receiver_id").isEqualTo(response.getReceiverId());
    }

    @Test
    void shouldReturnBadRequestIfChatNotFound() {
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .get().uri("/api/v1/chats/" + "123e4567-e89b-12d3-a456-426614174000")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnBadRequestIfChatNotBelongToUser() {
        ChatResponse response = this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("2")))
                .post().uri("/api/v1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ChatRequest(3))
                .exchange()
                .expectBody(ChatResponse.class)
                .returnResult().getResponseBody();
        assert response != null;
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("2")))
                .get().uri("/api/v1/chats/" + response.getChatId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnUnauthorizedWhenGetChatByChatId() {
        ChatResponse response = this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/chats")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ChatRequest(2))
                .exchange()
                .expectBody(ChatResponse.class)
                .returnResult().getResponseBody();
        assert response != null;
        this.webTestClient
                .mutateWith(csrf())
                .get().uri("/api/v1/chats/" + response.getChatId())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}