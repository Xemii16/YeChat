package com.yechat.chats.chat;

import com.yechat.chats.chat.exception.ChatAlreadyExistsException;
import com.yechat.chats.chat.exception.ChatNotFoundException;
import com.yechat.chats.chat.request.ChatRequest;
import com.yechat.chats.chat.response.ChatResponse;
import com.yechat.chats.user.UserClient;
import com.yechat.chats.user.exception.UserNotFoundException;
import com.yechat.chats.user.response.UserResponse;
import io.micrometer.common.lang.NonNullApi;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@NonNullApi
@RequiredArgsConstructor
public class ChatService {


    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final UserClient userClient;

    public ChatResponse createChat(ChatRequest request, Jwt jwt) throws ChatAlreadyExistsException {
        Integer userId = getUserId(jwt);
        UserResponse receiver = userClient.getUser(request.receiverId())
                .orElseThrow(() -> new UserNotFoundException(request.receiverId()));
        if (chatRepository.existsBySenderIdAndReceiverId(userId, receiver.id())) {
            throw new ChatAlreadyExistsException(userId, receiver.id());
        }
        Chat senderChat = createChat(userId, receiver.id());
        return chatMapper.toResponse(senderChat);
    }

    public List<ChatResponse> getChats(Jwt jwt) {
        Integer userId = getUserId(jwt);
        List<Chat> chats = chatRepository.findAllBySenderId(userId);
        return chats.stream()
                .map(chatMapper::toResponse)
                .toList();
    }

    public void deleteChat(Integer receiverId, Jwt jwt, boolean deleteAll) {
        Integer userId = getUserId(jwt);
        if (!chatRepository.existsBySenderIdAndReceiverId(userId, receiverId)) {
            throw new ChatNotFoundException(userId, receiverId);
        }
        if (deleteAll) {
            chatRepository.deleteBySenderIdAndReceiverId(receiverId, userId);
        }
        chatRepository.deleteBySenderIdAndReceiverId(userId, receiverId);
    }

    /**
     * Create a chat between two users
     *
     * @param senderId   sender id
     * @param receiverId receiver id
     * @return sender chat
     */
    private Chat createChat(Integer senderId, Integer receiverId) {
        UUID chatId = UUID.randomUUID();
        Chat senderChat = Chat.builder()
                .chatId(chatId)
                .senderId(senderId)
                .receiverId(receiverId)
                .build();
        Chat savedSenderChat = chatRepository.save(senderChat);
        Chat receiverChat = Chat.builder()
                .chatId(chatId)
                .senderId(receiverId)
                .receiverId(senderId)
                .build();
        chatRepository.save(receiverChat);
        return savedSenderChat;
    }

    private Integer getUserId(@NonNull Jwt jwt) {
        return Integer.parseInt(jwt.getSubject());
    }
}
