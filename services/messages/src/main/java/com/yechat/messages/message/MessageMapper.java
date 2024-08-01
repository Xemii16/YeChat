package com.yechat.messages.message;

import com.yechat.messages.message.response.MessageResponse;
import org.springframework.stereotype.Service;

@Service
public class MessageMapper {

    public MessageResponse toResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .chatId(message.getChatId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .build();
    }
}
