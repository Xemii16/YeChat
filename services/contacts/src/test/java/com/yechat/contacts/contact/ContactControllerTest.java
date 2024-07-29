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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private final List<Contact> contacts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        when(contactRepository.save((any(Contact.class))))
                .thenAnswer(invocation -> {
                    Contact contact = invocation.getArgument(0);
                    contact.setId(contacts.size() + 1);
                    contacts.add(contact);
                    return contact;
                });
        when(userClient.getUser(1))
                .thenReturn(java.util.Optional.of(new UserResponse(1, "test", "test", "test")));
        when(userClient.getUser(2))
                .thenReturn(java.util.Optional.of(new UserResponse(2, "test", "test", "test")));
        when(userClient.getUser(3))
                .thenReturn(java.util.Optional.empty());
        when(contactRepository.findAllByUserId(any(Integer.class)))
                .thenAnswer(invocation -> {
                    Integer userId = invocation.getArgument(0);
                    return contacts.stream()
                            .filter(contact -> contact.getUserId().equals(userId))
                            .toList();
                });
        when(contactRepository.findById(any(Integer.class)))
                .thenAnswer(invocation -> {
                    Integer id = invocation.getArgument(0);
                    return contacts.stream()
                            .filter(contact -> contact.getId().equals(id))
                            .findFirst();
                });
        doAnswer(invocation -> {
            Contact contact = invocation.getArgument(0);
            contacts.remove(contact);
            return null;
        }).when(contactRepository).delete(any(Contact.class));
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
                .andExpect(jsonPath("$.contact_id").value(2));
    }

    @Test
    void shouldReturnBadRequestWhenAddContactWithContactIdIsNull() throws Exception {
        String content = objectMapper.writeValueAsString(new ContactRequest(null));
        mockMvc.perform(post("/api/v1/contacts")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType("application/json")
                        .content(content)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenAddContactWithContactIdIsNonExists() throws Exception {
        String content = objectMapper.writeValueAsString(new ContactRequest(3));
        mockMvc.perform(post("/api/v1/contacts")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType("application/json")
                        .content(content)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenAddContactWithContactIdIsNegative() throws Exception {
        String content = objectMapper.writeValueAsString(new ContactRequest(-1));
        mockMvc.perform(post("/api/v1/contacts")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType("application/json")
                        .content(content)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnForbiddenWhenAddContactWithUserIsNotAuthenticated() throws Exception {
        String content = objectMapper.writeValueAsString(new ContactRequest(2));
        mockMvc.perform(post("/api/v1/contacts")
                        .contentType("application/json")
                        .content(content)
                )
                .andExpect(status().isForbidden()); // Will replace with 401
    }

    @Test
    void shouldReturnAllContactsWithAuthentication() throws Exception {
        this.contactRepository.save(Contact.builder()
                .userId(1)
                .contactId(2)
                .build()
        );
        mockMvc.perform(get("/api/v1/contacts")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].contact_id").value(2));
    }

    @Test
    void shouldReturnForbiddenWhenGetContactsWithUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/contacts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldDeleteContactThatExists() throws Exception {
        this.contactRepository.save(Contact.builder()
                .userId(1)
                .contactId(2)
                .build()
        );
        mockMvc.perform(delete("/api/v1/contacts/1")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                )
                .andExpect(status().isNoContent());
        assertThat(contactRepository.findById(1)).isEmpty();
    }
}