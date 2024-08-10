package com.yechat.messages.exception;

public class ServiceNotAvailableException extends RuntimeException {

    public ServiceNotAvailableException(String serviceName) {
        super(String.format("Service %s is not available", serviceName));
    }
}
