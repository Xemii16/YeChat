package com.yechat.messages.message;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TestController {

    private final MessageRepository messageRepository;

    @GetMapping("/test")
    public String test() {
        return messageRepository.save(Message.builder()
                .id(UUID.randomUUID())
                .chatId(UUID.randomUUID())
                .senderId(1)
                .content("Hello")
                .timestamp(System.currentTimeMillis())
                .build()).toString();
    }
}
