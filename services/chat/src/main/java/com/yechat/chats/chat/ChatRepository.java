package com.yechat.chats.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Integer> {
    boolean existsBySenderIdAndReceiverId(Integer senderId, Integer receiverId);

    List<Chat> findAllBySenderId(Integer senderId);

    void deleteBySenderIdAndReceiverId(Integer senderId, Integer receiverId);
}
