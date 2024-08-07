package com.yechat.contacts.contact;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yechat.contacts.contact.request.ContactRequest;
import com.yechat.contacts.exception.ValidationExceptionHandler;
import com.yechat.contacts.user.UserClient;
import com.yechat.contacts.user.UserResponse;
import com.yechat.contacts.user.exception.UserNotFoundException;
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

@WebFluxTest(ContactController.class)
@Import({ContactMapper.class, ContactService.class, ValidationExceptionHandler.class})
@AutoConfigureDataR2dbc
@AutoConfigureWebTestClient
@Testcontainers
class ContactControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private ContactRepository contactRepository;
    @MockBean
    private UserClient userClient;
    @Autowired
    private ObjectMapper objectMapper;

    @Container
    final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("contacts")
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
        when(userClient.getUser(1))
                .thenReturn(Mono.just(new UserResponse(1, "test", "test", "test")));
        when(userClient.getUser(2))
                .thenReturn(Mono.just(new UserResponse(2, "test", "test", "test")));
        when(userClient.getUser(3))
                .thenReturn(Mono.error(new UserNotFoundException(3)));
    }

    @AfterEach
    void tearDown() {
        contactRepository.deleteAll().block();
    }

    @Test
    void shouldCreateContactSuccessfully() throws Exception {
        String content = objectMapper.writeValueAsString(new ContactRequest(2));
        webTestClient
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .mutateWith(csrf())
                .post().uri("/api/v1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ContactRequest(2))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.contact_id").isEqualTo(2);
    }

    @Test
    void shouldReturnBadRequestWhenAddContactWithContactIdIsNull() throws Exception {
        webTestClient
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .mutateWith(csrf())
                .post().uri("/api/v1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ContactRequest(null))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnBadRequestWhenAddContactWithContactIdIsNonExists() throws Exception {
        /*String content = objectMapper.writeValueAsString(new ContactRequest(3));
        mockMvc.perform(post("/api/v1/contacts")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType("application/json")
                        .content(content)
                )
                .andExpect(status().isBadRequest());*/
        webTestClient
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .mutateWith(csrf())
                .post().uri("/api/v1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ContactRequest(3))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnBadRequestWhenAddContactWithContactIdIsNegative() throws Exception {
        /*String content = objectMapper.writeValueAsString(new ContactRequest(-1));
        mockMvc.perform(post("/api/v1/contacts")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                        .contentType("application/json")
                        .content(content)
                )
                .andExpect(status().isBadRequest());*/
        webTestClient
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .mutateWith(csrf())
                .post().uri("/api/v1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ContactRequest(-1))
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnUnauthorizedWhenAddContactWithUserIsNotAuthenticated() throws Exception {
        /*String content = objectMapper.writeValueAsString(new ContactRequest(2));
        mockMvc.perform(post("/api/v1/contacts")
                        .contentType("application/json")
                        .content(content)
                )
                .andExpect(status().isForbidden()); // Will replace with 401*/
        webTestClient
                .mutateWith(csrf())
                .post().uri("/api/v1/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ContactRequest(2))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldReturnAllContactsWithAuthentication() throws Exception {
        Contact contact = this.contactRepository.save(Contact.builder()
                .userId(1)
                .contactId(2)
                .build()
        ).block();
        /*mockMvc.perform(get("/api/v1/contacts")
                        .with(jwt().jwt(jwt -> jwt.subject("1")))
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(contact.getId()))
                .andExpect(jsonPath("$[0].contact_id").value(contact.getContactId()));*/
        assert contact != null;
        webTestClient
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .mutateWith(csrf())
                .get().uri("/api/v1/contacts")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].id").isEqualTo(contact.getId())
                .jsonPath("$[0].contact_id").isEqualTo(contact.getContactId());
    }

    @Test
    void shouldReturnForbiddenWhenGetContactsWithUserIsNotAuthenticated() throws Exception {
        /*mockMvc.perform(get("/api/v1/contacts"))
                .andExpect(status().isUnauthorized());*/
        webTestClient
                .mutateWith(csrf())
                .get().uri("/api/v1/contacts")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldDeleteContactThatExists() throws Exception {
        Contact contact = this.contactRepository.save(Contact.builder()
                .userId(1)
                .contactId(2)
                .build()
        ).block();
        /*mockMvc.perform(delete("/api/v1/contacts/" + contact.getContactId())
                        .with(jwt().jwt(jwt -> jwt.subject(contact.getUserId().toString())))
                )
                .andExpect(status().isNoContent());
        assertThat(contactRepository.findById(1)).isEmpty();*/
        assert contact != null;
        webTestClient
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject(contact.getUserId().toString())))
                .mutateWith(csrf())
                .delete().uri("/api/v1/contacts/" + contact.getContactId())
                .exchange()
                .expectStatus().isNoContent();
        assertThat(contactRepository.findById(1).block()).isNull();
    }
}