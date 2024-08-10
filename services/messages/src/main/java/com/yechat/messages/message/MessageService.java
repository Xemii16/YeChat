package com.yechat.messages.message;

import com.yechat.messages.chat.ChatClient;
import com.yechat.messages.chat.exception.ChatNotFoundException;
import com.yechat.messages.message.exception.MessageEmptyException;
import com.yechat.messages.message.request.MessageRequest;
import com.yechat.messages.message.response.MessageResponse;
import jakarta.ws.rs.client.ResponseProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
@RequiredArgsConstructor
@Profile("!testing")
public class MessageService {

    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final ChatClient chatClient;

    public Mono<MessageResponse> sendMessage(MessageRequest request, Jwt jwt) {
        if (request.chatId() == null) {
            return Mono.error(new ResponseStatusException(BAD_REQUEST, "chatId is required"));
        }
        if (request.content().isEmpty()) {
            return Mono.error(new MessageEmptyException());
        }
        return chatClient.getChat(request.chatId())
                .flatMap(chatResponse -> messageRepository.save(
                        Message.builder()
                                .id(UUID.randomUUID())
                                .chatId(request.chatId())
                                .senderId(Integer.parseInt(jwt.getClaim("sub")))
                                .content(request.content())
                                .timestamp(System.currentTimeMillis())
                                .build()
                ))
                .map(messageMapper::toResponse);
    }

    // TODO pagination and check if chat belongs to user
    public Flux<MessageResponse> getMessage(UUID chatId, Jwt jwt) {
        return chatClient.getChat(chatId)
                .flatMapMany(chatResponse -> messageRepository.findAllByChatId(chatId))
                .map(messageMapper::toResponse);
    }
}
