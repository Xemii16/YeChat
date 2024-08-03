package com.yechat.contacts.user;

import com.netflix.discovery.EurekaClient;
import com.yechat.contacts.UserContainerConfiguration;
import com.yechat.contacts.configuration.RestClientConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.CommonsClientAutoConfiguration;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.commons.config.CommonsConfigAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaAutoServiceRegistration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

@SpringBootTest(classes = {
        RestUserClient.class,
        EurekaClientAutoConfiguration.class,
        EurekaAutoServiceRegistration.class,
        CommonsClientAutoConfiguration.class,
        CommonsConfigAutoConfiguration.class,
})
@Import({
        RestClientConfiguration.class,
        UserContainerConfiguration.class,
        EurekaDiscoveryClient.class
})
@ExtendWith(SpringExtension.class)
@Testcontainers
class UserClientTest {

    @Container
    public static final GenericContainer eurekaServer =
            new GenericContainer("xemii16/yechat-discovery:0.0.1-SNAPSHOT")
                    .withExposedPorts(8761);

    @DynamicPropertySource
    static void registerCassandraProperties(DynamicPropertyRegistry registry) {
        registry.add("eureka.client.serviceUrl.defaultZone",
                () -> "http://localhost:"
                        + eurekaServer.getFirstMappedPort().toString()
                        + "/eureka");
    }

    @Autowired
    private DiscoveryClient discoveryClient;

    @BeforeEach
    void setUp() {
        await().atMost(60, SECONDS).until(() -> !discoveryClient.getServices().isEmpty());
    }

    @Autowired
    @Lazy
    private EurekaClient eurekaClient;

    @Autowired
    private UserClient userClient;


    @Test
    void getUser() {
        this.userClient.getUser(123);
    }
}