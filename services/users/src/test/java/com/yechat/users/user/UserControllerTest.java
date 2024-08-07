package com.yechat.users.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yechat.users.user.request.UserRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({UserService.class, UserMapper.class})
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Testcontainers
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private final User user = User.builder()
            .email("test@test.com")
            .firstname("Test")
            .lastname("Testing")
            .username("test")
            .build();

    @Container
    @ServiceConnection
    final static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("users")
            .withUsername("user")
            .withPassword("password");


    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void shouldReturnUserThatExists() throws Exception {
        User user = this.userRepository.save(this.user);
        this.mockMvc.perform(get("/api/v1/users/" + user.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.firstname").value(user.getFirstname()))
                .andExpect(jsonPath("$.lastname").value(user.getLastname()));
    }

    @Test
    void shouldReturnNotFoundIfUserDoesNotExist() throws Exception {
        this.mockMvc.perform(get("/api/v1/users/2"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldRegisterUser() throws Exception {
        UserRequest request = new UserRequest(
                "test",
                "test",
                "test",
                "test@mail.test"
        );
        this.mockMvc.perform(
                        post("/api/v1/users")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(request.username()))
                .andExpect(jsonPath("$.firstname").value(request.firstname()))
                .andExpect(jsonPath("$.lastname").value(request.lastname()))
                .andExpect(jsonPath("$.id").isNumber());
    }

    @Test
    void shouldReturnBadRequestIfUserExists() throws Exception {
        User user = this.userRepository.save(this.user);
        UserRequest request = new UserRequest(
                user.getUsername(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail()
        );
        this.mockMvc.perform(
                        post("/api/v1/users")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfRequestFieldsEmpty() throws Exception {
        UserRequest request = new UserRequest(
                "",
                "",
                "",
                ""
        );
        this.mockMvc.perform(
                        post("/api/v1/users")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstname").isNotEmpty())
                .andExpect(jsonPath("$.lastname").isNotEmpty())
                .andExpect(jsonPath("$.username").isNotEmpty())
                .andExpect(jsonPath("$.email").isNotEmpty());
    }

    @Test
    void shouldReturnBadRequestIfRequestDoesNotProvide() throws Exception {
        this.mockMvc.perform(
                        post("/api/v1/users")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestIfRequestHasInvalidEmail() throws Exception {
        UserRequest request = new UserRequest(
                "test",
                "test",
                "test",
                "test"
        );
        this.mockMvc.perform(
                        post("/api/v1/users")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.username").doesNotExist())
                .andExpect(jsonPath("$.firstname").doesNotExist())
                .andExpect(jsonPath("$.lastname").doesNotExist())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void shouldReturnBadRequestIfRequestHadFieldsTooLong() throws Exception {
        UserRequest request = new UserRequest(
                "aaaaaaaaaabbbbbbbbbbccccccccccddd",
                "aaaaaaaaaabbbbbbbbbbccccccccccddd",
                "aaaaaaaaaabbbbbbbbbbccccccccccddd",
                "aaaaaaaaaa@gege.com"
        );
        this.mockMvc.perform(
                        post("/api/v1/users")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.firstname").exists())
                .andExpect(jsonPath("$.lastname").exists())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    void shouldReturnBadRequestIfRequestHadFieldsTooShort() throws Exception {
        UserRequest request = new UserRequest(
                "a",
                "b",
                "c",
                "aaaaaaaaaa@gege.com"
        );
        this.mockMvc.perform(
                        post("/api/v1/users")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").doesNotExist())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.firstname").exists())
                .andExpect(jsonPath("$.lastname").exists())
                .andExpect(jsonPath("$.id").doesNotExist());
    }
}