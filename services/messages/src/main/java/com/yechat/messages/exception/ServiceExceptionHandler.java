package com.yechat.messages.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.ServiceUnavailableException;

@RestControllerAdvice
public class ServiceExceptionHandler {

    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseStatusException handleServiceUnavailableException(ServiceUnavailableException e) {
        return new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }
}
