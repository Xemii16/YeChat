package com.yechat.chats.chat;

import com.yechat.chats.chat.exception.ChatAlreadyExistsException;
import com.yechat.chats.chat.exception.ChatNotFoundException;
import com.yechat.chats.chat.request.ChatRequest;
import com.yechat.chats.chat.response.ChatResponse;
import com.yechat.chats.user.UserClient;
import io.micrometer.common.lang.NonNullApi;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@NonNullApi
@RequiredArgsConstructor
@Profile("!testing")
public class ChatService {


    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final UserClient userClient;

    public Mono<ChatResponse> createChat(ChatRequest request, Jwt jwt) throws ChatAlreadyExistsException {
        Integer userId = getUserId(jwt);
        return userClient.getUser(request.receiverId())
                .flatMap(receiver -> chatRepository.existsBySenderIdAndReceiverId(userId, receiver.id())
                        .flatMap(exists -> {
                            if (exists) {
                                return Mono.error(new ChatAlreadyExistsException(userId, receiver.id()));
                            }
                            return createChat(userId, receiver.id())
                                    .map(chatMapper::toResponse);
                        }));
    }

    public Flux<ChatResponse> getChats(Jwt jwt) {
        Integer userId = getUserId(jwt);
        return chatRepository.findAllBySenderId(userId)
                .map(chatMapper::toResponse);
    }

    public Mono<Void> deleteChat(Integer receiverId, Jwt jwt, boolean deleteAll) {
        Integer userId = getUserId(jwt);
        return chatRepository
                .existsBySenderIdAndReceiverId(userId, receiverId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new ChatNotFoundException(userId, receiverId));
                    }
                    Mono<Void> senderChatMono = chatRepository.deleteBySenderIdAndReceiverId(userId, receiverId);
                    if (deleteAll) {
                        return senderChatMono
                                .then(chatRepository.deleteBySenderIdAndReceiverId(receiverId, userId));
                    }
                    return senderChatMono;
                });
    }

    private Mono<Chat> createChat(Integer senderId, Integer receiverId) {
        UUID chatId = UUID.randomUUID();
        Chat senderChat = Chat.builder()
                .chatId(chatId)
                .senderId(senderId)
                .receiverId(receiverId)
                .build();
        Chat receiverChat = Chat.builder()
                .chatId(chatId)
                .senderId(receiverId)
                .receiverId(senderId)
                .build();
        return chatRepository.save(receiverChat)
                .then(chatRepository.save(senderChat));

    }

    private Integer getUserId(@NonNull Jwt jwt) {
        return Integer.parseInt(jwt.getSubject());
    }
}
