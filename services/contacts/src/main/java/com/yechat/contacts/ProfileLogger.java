package com.yechat.contacts;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProfileLogger implements ApplicationRunner {

    private final Environment environment;

    @Override
    public void run(ApplicationArguments args) {
        boolean isTestingProfileEnable = Arrays
                .asList(environment.getActiveProfiles())
                .contains("testing");
        if (isTestingProfileEnable) {
            spamWarn("Testing profile is enabled");
        }
    }

    private void spamWarn(String message) {
        for (int i = 0; i <= 10; i++) {
            log.warn(message);
        }
    }
}
