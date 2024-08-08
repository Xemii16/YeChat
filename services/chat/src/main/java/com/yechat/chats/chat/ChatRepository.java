package com.yechat.chats.chat;

import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Profile("!testing")
public interface ChatRepository extends R2dbcRepository<Chat, Integer> {
    Mono<Boolean> existsBySenderIdAndReceiverId(Integer senderId, Integer receiverId);

    Flux<Chat> findAllBySenderId(Integer senderId);

    Mono<Void> deleteBySenderIdAndReceiverId(Integer senderId, Integer receiverId);
}
