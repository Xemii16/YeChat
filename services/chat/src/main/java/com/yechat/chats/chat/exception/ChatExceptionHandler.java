package com.yechat.chats.chat.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ChatExceptionHandler {

    @ExceptionHandler(ChatBadRequestException.class)
    public ResponseEntity<Map<String, Object>> handleChatAlreadyExistsException(ChatBadRequestException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
