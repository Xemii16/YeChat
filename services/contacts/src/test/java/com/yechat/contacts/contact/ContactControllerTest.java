package com.yechat.contacts.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yechat.contacts.contact.request.ContactRequest;
import com.yechat.contacts.user.UserClient;
import com.yechat.contacts.user.UserResponse;
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
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContactController.class)
@Import({ContactMapper.class, ContactService.class})
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Testcontainers
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ContactRepository contactRepository;
    @MockBean
    private UserClient userClient;
    @Autowired
    private ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("contacts")
            .withUsername("user")
            .withPassword("password");

    @BeforeEach
    void setUp() {
        when(userClient.getUser(1))
                .thenReturn(java.util.Optional.of(new UserResponse(1, "test", "test", "test")));
        when(userClient.getUser(2))
                .thenReturn(java.util.Optional.of(new UserResponse(2, "test", "test", "test")));
        when(userClient.getUser(3))
                .thenReturn(java.util.Optional.empty());
    }

    @AfterEach
    void tearDown() {
        contactRepository.deleteAll();
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
        Contact contact = this.contactRepository.save(Contact.builder()
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
                .andExpect(jsonPath("$[0].id").value(contact.getId()))
                .andExpect(jsonPath("$[0].contact_id").value(contact.getContactId()));
    }

    @Test
    void shouldReturnForbiddenWhenGetContactsWithUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/contacts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldDeleteContactThatExists() throws Exception {
        Contact contact = this.contactRepository.save(Contact.builder()
                .userId(1)
                .contactId(2)
                .build()
        );
        mockMvc.perform(delete("/api/v1/contacts/" + contact.getContactId())
                        .with(jwt().jwt(jwt -> jwt.subject(contact.getUserId().toString())))
                )
                .andExpect(status().isNoContent());
        assertThat(contactRepository.findById(1)).isEmpty();
    }
}