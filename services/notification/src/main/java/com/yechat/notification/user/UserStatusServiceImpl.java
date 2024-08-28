package com.yechat.notification.user;

import com.yechat.notification.chat.ChatClient;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class UserStatusServiceImpl implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final ChatClient chatClient;

    @Override
    public Flux<UserStatus> findStatusesByCurrentUser(Jwt currentUser) {
        return chatClient
                .getChats(currentUser)
                .flatMap(chat -> userStatusRepository.findByUserId(chat.receiverId()));
    }
}
