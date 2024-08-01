package com.yechat.messages.message;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface MessageRepository extends CrudRepository<Message, Integer> {
    Collection<Message> findAllByChatIdOrderByTimestampDesc(UUID chatId);
}
