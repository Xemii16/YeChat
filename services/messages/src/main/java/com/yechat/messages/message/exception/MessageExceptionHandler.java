package com.yechat.messages.message.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class MessageExceptionHandler {

    @ExceptionHandler(BadMessageException.class)
    public ResponseEntity<Map<String, Object>> handleBadMessageException(BadMessageException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
