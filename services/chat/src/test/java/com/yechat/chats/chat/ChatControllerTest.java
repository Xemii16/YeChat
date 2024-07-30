package com.yechat.chats.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yechat.chats.chat.request.ChatRequest;
import com.yechat.chats.user.UserClient;
import com.yechat.chats.user.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
@Import({ChatService.class, ChatMapper.class})
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserClient userClient;
    @MockBean
    private ChatRepository chatRepository;

    @BeforeEach
    void setUp() {
        List<Chat> chats = new ArrayList<>();
        when(chatRepository.save(any(Chat.class)))
                .thenAnswer(invocation -> {
                    Chat chat = invocation.getArgument(0);
                    chat.setId(chats.size() + 1);
                    chats.add(chat);
                    return chat;
                });
        when(userClient.getUser(2))
                .thenReturn(Optional.of(new UserResponse(2, "test", "test", "test")));
        when(userClient.getUser(3))
                .thenReturn(Optional.empty());
        when(chatRepository.existsBySenderIdAndReceiverId(any(Integer.class), any(Integer.class)))
                .thenAnswer(invocation -> {
                    Integer senderId = invocation.getArgument(0, Integer.class);
                    Integer receiverId = invocation.getArgument(1, Integer.class);
                    return chats.stream()
                            .anyMatch(chat -> chat.getSenderId().equals(senderId) && chat.getReceiverId().equals(receiverId));
                });
        when(chatRepository.existsBySenderIdAndReceiverId(1, 3))
                .thenReturn(false);
        when(chatRepository.findAllBySenderId(any(Integer.class)))
                .thenAnswer(invocation -> {
                    Integer senderId = invocation.getArgument(0, Integer.class);
                    return chats.stream()
                            .filter(chat -> chat.getSenderId().equals(senderId))
                            .toList();
                });
        doAnswer(invocation -> {
            Integer senderId = invocation.getArgument(0, Integer.class);
            Integer receiverId = invocation.getArgument(1, Integer.class);
            chats.removeIf(chat -> chat.getSenderId().equals(senderId) && chat.getReceiverId().equals(receiverId));
            return null;
        }).when(chatRepository).deleteBySenderIdAndReceiverId(any(Integer.class), any(Integer.class));
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