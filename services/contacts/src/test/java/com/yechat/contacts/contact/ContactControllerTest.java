package com.yechat.contacts.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yechat.contacts.contact.request.ContactRequest;
import com.yechat.contacts.user.UserClient;
import com.yechat.contacts.user.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
@Import({ContactMapper.class, ContactService.class})
@AutoConfigureMockMvc
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ContactRepository contactRepository;
    @MockBean
    private UserClient userClient;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        when(contactRepository.save((any(Contact.class))))
                .thenAnswer(invocation -> {
                    Contact contact = invocation.getArgument(0);
                    contact.setId(1);
                    return contact;
                });
        when(userClient.getUser(1))
                .thenReturn(java.util.Optional.of(new UserResponse(1, "test", "test", "test")));
        when(userClient.getUser(2))
                .thenReturn(java.util.Optional.of(new UserResponse(2, "test", "test", "test")));
        when(userClient.getUser(3))
                .thenReturn(java.util.Optional.empty());
    }

    @Test
    void shouldCreateContactSuccessfully() throws Exception {
        String content = objectMapper.writeValueAsString(new ContactRequest(2));
        mockMvc.perform(post("/api/v1/contacts")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType("application/json")
                        .content(content)
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.contactId").value(2));
    }

    @Test
    void shouldReturnBadRequestWhenContactIdIsNull() throws Exception {
        String content = objectMapper.writeValueAsString(new ContactRequest(null));
        mockMvc.perform(post("/api/v1/contacts")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType("application/json")
                        .content(content)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenContactIdIsNonExists() throws Exception {
        String content = objectMapper.writeValueAsString(new ContactRequest(3));
        mockMvc.perform(post("/api/v1/contacts")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType("application/json")
                        .content(content)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenContactIdIsNegative() throws Exception {
        String content = objectMapper.writeValueAsString(new ContactRequest(-1));
        mockMvc.perform(post("/api/v1/contacts")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType("application/json")
                        .content(content)
                )
                .andExpect(status().isBadRequest());
    }
}