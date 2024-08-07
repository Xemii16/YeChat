package com.yechat.contacts.user.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends UserException {

    public UserNotFoundException(Integer id) {
        super("User not found with id: " + id, HttpStatus.NOT_FOUND);
    }
}
