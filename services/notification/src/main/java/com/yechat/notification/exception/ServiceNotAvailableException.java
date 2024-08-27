package com.yechat.notification.exception;

public class ServiceNotAvailableException extends RuntimeException {

    public ServiceNotAvailableException(String serviceName) {
        super(String.format("Service %s is not available", serviceName));
    }
}
