package com.yechat.chats;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
public class ChatApplicationTests {

    @Autowired
    Environment env;

    @BeforeEach
    void setUp() {
        assert env != null;
    }

    @Test
    void assertThatDevelopmentProfilesNotActive() {
        assert !env.matchesProfiles("development", "testing");
    }
}
