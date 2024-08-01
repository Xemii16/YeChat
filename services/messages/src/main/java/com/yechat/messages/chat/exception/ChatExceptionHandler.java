package com.yechat.messages.chat.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ChatExceptionHandler {

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleChatNotFoundException(ChatNotFoundException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
