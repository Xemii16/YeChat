package com.yechat.chats.chat;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_id_seq")
    @SequenceGenerator(name = "chat_id_seq", sequenceName = "chat_id_seq", allocationSize = 1)
    private Integer id;
    private UUID chatId;
    private Integer senderId;
    private Integer receiverId;
}
