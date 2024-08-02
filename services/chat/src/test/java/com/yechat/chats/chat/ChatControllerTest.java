package com.yechat.chats.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yechat.chats.chat.request.ChatRequest;
import com.yechat.chats.user.UserClient;
import com.yechat.chats.user.response.UserResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@Import({ChatService.class, ChatMapper.class})
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Testcontainers
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserClient userClient;
    @Autowired
    private ChatRepository chatRepository;

    @Container
    @ServiceConnection
    final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("chats")
            .withUsername("user")
            .withPassword("password");

    @BeforeEach
    void setUp() {
        when(userClient.getUser(2))
                .thenReturn(Optional.of(new UserResponse(2, "test", "test", "test")));
        when(userClient.getUser(3))
                .thenReturn(Optional.empty());
    }

    @AfterEach
    void tearDown() {
        chatRepository.deleteAll();
    }

    @Test
    void shouldCreateChatSuccessfully() throws Exception {
        String content = objectMapper.writeValueAsString(new ChatRequest(2));
        this.mockMvc.perform(post("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chat_id").isString())
                .andExpect(jsonPath("$.receiver_id").value(2));
    }

    @Test
    void shouldReturnBadRequestWhenReceiverNotFound() throws Exception {
        String content = objectMapper.writeValueAsString(new ChatRequest(3));
        this.mockMvc.perform(post("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnForbiddenWhenUserNotAuthenticated() throws Exception {
        String content = objectMapper.writeValueAsString(new ChatRequest(2));
        this.mockMvc.perform(post("/api/v1/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldReturnBadRequestWhenChatExists() throws Exception {
        String content = objectMapper.writeValueAsString(new ChatRequest(2));
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
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnAllChats() throws Exception {
        this.mockMvc.perform(post("/api/v1/chats")
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
                .andExpect(jsonPath("$[0].receiver_id").value(2));
    }

    @Test
    void shouldReturnAuthorizedWhenUserNotAuthenticated() throws Exception {
        this.mockMvc.perform(get("/api/v1/chats")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturnEmptyListWhenNoChats() throws Exception {
        this.mockMvc.perform(get("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldDeleteChatForSender() throws Exception {
        this.mockMvc.perform(post("/api/v1/chats")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatRequest(2)))
                )
                .andExpect(status().isOk());
        this.mockMvc.perform(delete("/api/v1/chats/2")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                )
                .andExpect(status().isNoContent());
        assertThat(chatRepository.findAllBySenderId(1).isEmpty()).isTrue();
    }

    @Test
    void shouldReturnBadRequestWhenChatNotExists() throws Exception {
        this.mockMvc.perform(delete("/api/v1/chats/2")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnForbiddenWhenDeleteUserAndNotAuthenticated() throws Exception {
        this.mockMvc.perform(delete("/api/v1/chats/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldDeleteChatForBothUsers() throws Exception {
        this.mockMvc.perform(post("/api/v1/chats")
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
        assertThat(chatRepository.findAllBySenderId(2).isEmpty()).isTrue();
    }
}