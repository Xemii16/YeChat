package com.yechat.messages.message;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.yechat.messages.chat.ChatClient;
import com.yechat.messages.chat.exception.ChatNotFoundException;
import com.yechat.messages.chat.response.ChatResponse;
import com.yechat.messages.message.request.MessageRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.cassandra.AutoConfigureDataCassandra;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest(MessageController.class)
@Import({MessageMapper.class, MessageService.class})
@AutoConfigureDataCassandra
@AutoConfigureWebTestClient
@Testcontainers
class MessageControllerTest {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private MessageRepository messageRepository;
    @MockBean
    private ChatClient chatClient;
    private static final UUID CHAT_ID = UUID.randomUUID();
    private static final UUID CHAT_ID_THAT_NOT_BELONG_TO_USER = UUID.randomUUID();

    @Container
    @ServiceConnection
    final static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:latest")
            .withExposedPorts(9042);

    @DynamicPropertySource
    static void registerCassandraProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.cassandra.keyspace-name", () -> "messages");
        registry.add("spring.cassandra.contacts-points", () -> cassandra.getContactPoint().toString());
        registry.add("spring.cassandra.local-datacenter", cassandra::getLocalDatacenter);
    }

    @BeforeAll
    static void beforeAll() {
        createKeyspace(cassandra.getCluster());
    }

    @AfterEach
    void tearDown() {
        messageRepository.deleteAll()
                .block();
    }

    private static void createKeyspace(Cluster cluster) {
        try (Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS " + "messages" +
                    " WITH replication = \n" +
                    "{'class':'SimpleStrategy','replication_factor':'1'};");
        }
    }

    @BeforeEach
    void setUp() {
        when(chatClient.getChat(CHAT_ID))
                .thenReturn(Mono.just(new ChatResponse(CHAT_ID, 2)));
        when(chatClient.getChat(CHAT_ID_THAT_NOT_BELONG_TO_USER))
                .thenReturn(Mono.error(new ChatNotFoundException(CHAT_ID_THAT_NOT_BELONG_TO_USER)));
    }

    @Test
    void shouldSendMessageWithAuthentication() {
        MessageRequest request = new MessageRequest(CHAT_ID, "Hello, World!");
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.chat_id").isEqualTo(request.chatId().toString())
                .jsonPath("$.content").isEqualTo(request.content())
                .jsonPath("$.sender_id").isEqualTo(1)
                .jsonPath("$.timestamp").isNumber();

    }

    @Test
    void shouldUnauthorizedWhenSendMessageWithoutAuthentication() {
        MessageRequest request = new MessageRequest(CHAT_ID, "Hello, World!");
        this.webTestClient
                .mutateWith(csrf())
                .post().uri("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldBadRequestWhenSendMessageWithEmptyContent() {
        MessageRequest request = new MessageRequest(CHAT_ID, "");
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldBadRequestWhenSendMessageWithNonExistChatId() {
        MessageRequest request = new MessageRequest(UUID.randomUUID(), "");
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldBadRequestWhenSendMessageWithNullChatId() {
        MessageRequest request = new MessageRequest(null, "Hello world!");
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldBadRequestWhenSendMessageToChatThatNotBelongToUser() {
        MessageRequest request = new MessageRequest(CHAT_ID_THAT_NOT_BELONG_TO_USER, "Hello, World!");
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .post().uri("/api/v1/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void shouldReturnMessages() {
        saveTwoMessages();
        this.webTestClient
                .mutateWith(csrf())
                .mutateWith(mockJwt().jwt(jwt -> jwt.subject("1")))
                .get().uri("/api/v1/messages/" + CHAT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].id").exists()
                .jsonPath("$[0].chat_id").isEqualTo(CHAT_ID.toString())
                .jsonPath("$[0].sender_id").isEqualTo(1)
                .jsonPath("$[0].content").isEqualTo("Hello, World!")
                .jsonPath("$[0].timestamp").isNumber();
    }

    @Test
    @Disabled
        // TODO: Fix this test (maybe will delete it)
    void shouldReturnForbiddenWhenReturnMessages() {
        saveTwoMessages();
    }

    @Test
    void shouldReturnUnauthorizedWhenReturnMessages() {
        saveTwoMessages();
        this.webTestClient
                .mutateWith(csrf())
                .get().uri("/api/v1/messages" + CHAT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    private void saveTwoMessages() {
        Message.MessageBuilder message = Message.builder()
                .id(UUID.randomUUID())
                .chatId(CHAT_ID)
                .senderId(1)
                .content("Hello, World!")
                .timestamp(System.currentTimeMillis());
        this.messageRepository
                .save(message.build())
                .block();
        this.messageRepository
                .save(
                        message
                                .id(UUID.randomUUID())
                                .timestamp(System.currentTimeMillis() + 1000)
                                .build()
                )
                .block();
    }
}