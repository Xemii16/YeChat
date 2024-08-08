package com.yechat.chats.chat;

import com.yechat.chats.chat.exception.ChatExceptionHandler;
import com.yechat.chats.chat.request.ChatRequest;
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
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
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
    void shouldCreateChatSuccessfully() throws Exception {
        /*String content = objectMapper.writeValueAsString(new ChatRequest(2));
        this.mockMvc.perform(post("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chat_id").isString())
                .andExpect(jsonPath("$.receiver_id").value(2));*/
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
    void shouldReturnBadRequestWhenReceiverNotFound() throws Exception {
        /*String content = objectMapper.writeValueAsString(new ChatRequest(3));
        this.mockMvc.perform(post("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isBadRequest());*/
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
    void shouldReturnUnauthorizedWhenUserNotAuthenticated() throws Exception {
        /*String content = objectMapper.writeValueAsString(new ChatRequest(2));
        this.mockMvc.perform(post("/api/v1/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isForbidden());*/
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
        /*String content = objectMapper.writeValueAsString(new ChatRequest(2));
        this.mockMvc.perform(post("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isOk());
        this.mockMvc.perform(post("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isBadRequest());*/
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
    void shouldReturnAllChats() throws Exception {
        /*this.mockMvc.perform(post("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatRequest(2)))
                )
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].chat_id").isString())
                .andExpect(jsonPath("$[0].receiver_id").value(2));*/
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
    void shouldReturnAuthorizedWhenUserNotAuthenticated() throws Exception {
        /*this.mockMvc.perform(get("/api/v1/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());*/
        this.webTestClient
                .mutateWith(csrf())
                .get().uri("/api/v1/chats")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturnEmptyListWhenNoChats() throws Exception {
        /*this.mockMvc.perform(get("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());*/
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
    void shouldDeleteChatForSender() throws Exception {
        /*this.mockMvc.perform(post("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatRequest(2)))
                )
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/api/v1/chats/2")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                )
                .andExpect(status().isNoContent());
        assertThat(chatRepository.findAllBySenderId(1).isEmpty()).isTrue();*/
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
    void shouldReturnBadRequestWhenChatNotExists() throws Exception {
        /*this.mockMvc.perform(delete("/api/v1/chats/2")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                )
                .andExpect(status().isBadRequest());*/
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .delete().uri("/api/v1/chats/" + 2)
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void shouldReturnUnauthorizedWhenDeleteUserAndNotAuthenticated() throws Exception {
        /*this.mockMvc.perform(delete("/api/v1/chats/2"))
                .andExpect(status().isForbidden());*/
        this.webTestClient
                .mutateWith(csrf())
                .delete().uri("/api/v1/chats/" + 2)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldDeleteChatForBothUsers() throws Exception {
        /*this.mockMvc.perform(post("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatRequest(2)))
                )
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/api/v1/chats/2")
                        .queryParam("all", "true")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                )
                .andExpect(status().isNoContent());
        assertThat(chatRepository.findAllBySenderId(1).isEmpty()).isTrue();
        assertThat(chatRepository.findAllBySenderId(2).isEmpty()).isTrue();*/
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
}