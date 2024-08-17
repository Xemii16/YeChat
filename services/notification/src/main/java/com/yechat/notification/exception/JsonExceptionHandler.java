package com.yechat.notification.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Component
@Slf4j
public class JsonExceptionHandler {

    @ExceptionHandler(JsonProcessingException.class)
    public void handleJsonProcessingException(JsonProcessingException e) {
        log.error("Error processing JSON", e);
    }
}
