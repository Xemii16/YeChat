package com.yechat.messages.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yechat.messages.chat.ChatClient;
import com.yechat.messages.chat.exception.ChatClientFallbackFactory;
import com.yechat.messages.chat.response.ChatResponse;
import com.yechat.messages.message.request.MessageRequest;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@Import({MessageMapper.class, MessageService.class, ChatClientFallbackFactory.class})
@AutoConfigureMockMvc
class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MessageRepository messageRepository;
    @MockBean
    private ChatClient chatClient;
    private static final UUID CHAT_ID = UUID.randomUUID();
    private static final UUID CHAT_ID_THAT_NOT_BELONG_TO_USER = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        List<Message> messages = new ArrayList<>();
        when(messageRepository.save(any(Message.class)))
                .thenAnswer(invocation -> {
                    Message message = invocation.getArgument(0);
                    messages.add(message);
                    return message;
                });
        when(chatClient.getChat(CHAT_ID)).thenReturn(Optional.of(
                new ChatResponse(CHAT_ID, 2)
        ));
        when(chatClient.getChat(CHAT_ID_THAT_NOT_BELONG_TO_USER)).thenReturn(Optional.empty());
    }

    @Test
    void shouldSendMessageWithAuthentication() throws Exception {
        MessageRequest request = new MessageRequest(CHAT_ID, "Hello, World!");
        this.mockMvc.perform(post("/api/v1/messages")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.chat_id").value(request.chatId().toString()))
                .andExpect(jsonPath("$.content").value(request.content()))
                .andExpect(jsonPath("$.sender_id").value(1))
                .andExpect(jsonPath("$.timestamp").isNumber());
    }

    @Test
    void shouldForbiddenWhenSendMessageWithoutAuthentication() throws Exception {
        MessageRequest request = new MessageRequest(CHAT_ID, "Hello, World!");
        this.mockMvc.perform(post("/api/v1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldBadRequestWhenSendMessageWithEmptyContent() throws Exception {
        MessageRequest request = new MessageRequest(CHAT_ID, "");
        this.mockMvc.perform(post("/api/v1/messages")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestWhenSendMessageWithNonExistChatId() throws Exception {
        MessageRequest request = new MessageRequest(UUID.randomUUID(), "");
        this.mockMvc.perform(post("/api/v1/messages")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestWhenSendMessageWithNullChatId() throws Exception {
        MessageRequest request = new MessageRequest(null, "");
        this.mockMvc.perform(post("/api/v1/messages")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldBadRequestWhenSendMessageToChatThatNotBelongToUser() throws Exception {
        MessageRequest request = new MessageRequest(CHAT_ID_THAT_NOT_BELONG_TO_USER, "Hello, World!");
        this.mockMvc.perform(post("/api/v1/messages")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnOnlyFirstMessage() throws Exception {
        saveTwoMessages();
        this.mockMvc.perform(
                        get("/api/v1/messages/" + CHAT_ID)
                                .queryParam("number", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].chat_id").value(CHAT_ID.toString()))
                .andExpect(jsonPath("$[0].sender_id").value(1))
                .andExpect(jsonPath("$[0].content").value("Hello, World!"))
                .andExpect(jsonPath("$[0].timestamp").isNumber());
    }

    private void saveTwoMessages() {
        Message.MessageBuilder message = Message.builder()
                .id(UUID.randomUUID())
                .chatId(CHAT_ID)
                .senderId(1)
                .content("Hello, World!")
                .timestamp(System.currentTimeMillis());
        this.messageRepository.save(message.build());
        this.messageRepository.save(message
                .id(UUID.randomUUID())
                .timestamp(System.currentTimeMillis() + 1000)
                .build()
        );
    }
}