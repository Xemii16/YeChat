package com.yechat.messages.message;

import com.yechat.messages.chat.ChatClient;
import com.yechat.messages.chat.exception.ChatNotFoundException;
import com.yechat.messages.message.exception.MessageEmptyException;
import com.yechat.messages.message.request.MessageRequest;
import com.yechat.messages.message.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
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

    // TODO pagination and check if chat belongs to user
    public List<MessageResponse> getMessage(UUID chatId, Jwt jwt) {
        if (chatClient.getChat(chatId).isEmpty()) {
            throw new ChatNotFoundException(chatId);
        }
        if (chatClient.getChat(chatId).isEmpty()) {
            return List.of();
        }
        return messageRepository.findAllByChatId(chatId).stream()
                .map(messageMapper::toResponse)
                .toList();
    }
}
