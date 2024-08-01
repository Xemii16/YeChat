package com.yechat.messages.message;

import com.yechat.messages.chat.ChatClient;
import com.yechat.messages.chat.exception.ChatNotFoundException;
import com.yechat.messages.message.exception.MessageEmptyException;
import com.yechat.messages.message.request.MessageRequest;
import com.yechat.messages.message.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ChatClient chatClient;

    public MessageResponse sendMessage(MessageRequest request, Jwt jwt) {
        if (request.content().isEmpty()) {
            throw new MessageEmptyException();
        }
        if (chatClient.getChat(request.chatId()).isEmpty()){
            throw new ChatNotFoundException(request.chatId());
        }
        Message message = messageRepository.save(
                Message.builder()
                        .id(UUID.randomUUID())
                        .chatId(request.chatId())
                        .senderId(Integer.parseInt(jwt.getClaim("sub")))
                        .content(request.content())
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
        return messageMapper.toResponse(message);
    }

    public List<MessageResponse> getMessage(String chatId, Jwt jwt, int page, int size) {
        if (chatClient.getChat(UUID.fromString(chatId)).isEmpty()) {
            throw new ChatNotFoundException(UUID.fromString(chatId));
        }
        if (page < 0) {
            return messageRepository.findAllByChatIdOrderByTimestampDesc(UUID.fromString(chatId)).stream()
                    .map(messageMapper::toResponse)
                    .toList();
        }
        // TODO: Implement pagination and delete condition below for optimization
        return List.of();
    };
}
