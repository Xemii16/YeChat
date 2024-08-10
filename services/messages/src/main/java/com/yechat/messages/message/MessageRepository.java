package com.yechat.messages.message;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.UUID;

@Repository
@Profile("!testing")
public interface MessageRepository extends ReactiveCrudRepository<Message, Integer> {

    Flux<Message> findAllByChatId(UUID chatId);
}
