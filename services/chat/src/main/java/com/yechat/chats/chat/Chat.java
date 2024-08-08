package com.yechat.chats.chat;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "chats")
public class Chat {

    @Id
    private Integer id;
    @Column("chat_id")
    private UUID chatId;
    @Column("sender_id")
    private Integer senderId;
    @Column("receiver_id")
    private Integer receiverId;
}
