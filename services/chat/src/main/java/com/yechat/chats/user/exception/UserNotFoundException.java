package com.yechat.chats.user.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Integer id) {
        super("User not found with id: " + id);
    }
}
