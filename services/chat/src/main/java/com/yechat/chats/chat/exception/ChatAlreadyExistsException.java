package com.yechat.chats.chat.exception;

public class ChatAlreadyExistsException extends ChatBadRequestException {
    public ChatAlreadyExistsException(Integer userId, Integer receiverId) {
        super(String.format("Chat between %d and %d (id`s users) already exists", userId, receiverId));
    }
}
