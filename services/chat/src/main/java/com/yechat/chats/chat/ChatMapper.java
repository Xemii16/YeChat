package com.yechat.chats.chat;

import com.yechat.chats.chat.response.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class ChatMapper {
    public ChatResponse toResponse(Chat chat) {
        return ChatResponse.builder()
                .chatId(chat.getChatId())
                .receiverId(chat.getReceiverId())
                .build();
    }
}
