package com.yechat.users.user.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(String identifier) {
        super("User not found with identifier: " + identifier, HttpStatus.NOT_FOUND);
    }
}
