package com.yechat.messages.message.exception;

public class MessageEmptyException extends BadMessageException {
    public MessageEmptyException() {
        super("Message content cannot be empty");
    }
}
