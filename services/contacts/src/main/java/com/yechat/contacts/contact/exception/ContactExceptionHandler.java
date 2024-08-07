package com.yechat.contacts.contact.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ContactExceptionHandler {

    @ExceptionHandler(ContactNotExistException.class)
    public ResponseEntity<Map<String, Object>> handleContactNotFoundException(ContactNotExistException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
