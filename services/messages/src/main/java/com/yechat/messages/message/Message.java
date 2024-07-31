package com.yechat.messages.message;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("messages")
public class Message {

    @Id
    private UUID id;
    private UUID chatId;
    private Integer senderId;
    private String content;
    private Long timestamp;
}
