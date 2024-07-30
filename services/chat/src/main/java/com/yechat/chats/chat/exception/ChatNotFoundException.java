package com.yechat.chats.chat.exception;

public class ChatNotFoundException extends ChatBadRequestException {
    public ChatNotFoundException(Integer userId, Integer receiverId) {
        super(String.format("Chat between %d and %d (id`s users) not found", userId, receiverId));
    }
}
