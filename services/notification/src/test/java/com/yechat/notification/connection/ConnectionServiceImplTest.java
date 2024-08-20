package com.yechat.notification.connection;

import com.yechat.notification.JwtAuthenticationTestConfiguration;
import com.yechat.notification.configuration.SecurityConfiguration;
import com.yechat.notification.rsocket.jwt.JwtRSocketRequesterRepository;
import com.yechat.notification.rsocket.jwt.LocalJwtRSocketRequesterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.test.StepVerifier;

import java.time.Instant;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(
        classes = {
                ConnectionServiceImpl.class,
                LocalJwtRSocketRequesterRepository.class,
                SecurityConfiguration.class
        }
)
@Import(JwtAuthenticationTestConfiguration.class)
@EnableReactiveMethodSecurity
class ConnectionServiceImplTest {

    @Autowired
    private ConnectionService connectionService;
    @Autowired
    private JwtRSocketRequesterRepository repository;

    @Mock
    private RSocketRequester requester;

    @BeforeEach
    void setUp() {
        assert connectionService != null;
        assert requester != null;
        assert repository != null;
    }

    @Test
    void shouldAddToRepositorySuccessfully() {
        StepVerifier
                .create(connectionService.connect(
                        Jwt.withTokenValue("token")
                                .subject("subject")
                                .header("test", "test")
                                .expiresAt(Instant.now().plusSeconds(60))
                                .notBefore(Instant.now().minusSeconds(60))
                                .build(),
                        requester
                ))
                .expectComplete()
                .verify();
        assertThat(repository.findBySubject("subject").block()).isEqualTo(requester);
    }

    @Test
    void shouldAccessDeniedWhenConnectWithBadJwt() {
        StepVerifier
                .create(connectionService.connect(
                        Jwt.withTokenValue("token")
                                .subject("subject")
                                .header("test", "test")
                                .expiresAt(Instant.now().plusSeconds(120))
                                .notBefore(Instant.now().plusSeconds(60))
                                .build(),
                        requester
                ))
                .expectError(AuthorizationDeniedException.class)
                .verify();
        assertThat(repository.findBySubject("subject").block()).isNull();
    }
}