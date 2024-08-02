package com.yechat.users.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({UserService.class, UserMapper.class})
@AutoConfigureMockMvc
@AutoConfigureDataJpa
@Testcontainers
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;

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
}