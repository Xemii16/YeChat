package com.yechat.messages.message.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private UUID id;
    @JsonProperty("chat_id")
    private UUID chatId;
    @JsonProperty("sender_id")
    private Integer senderId;
    private String content;
    private Long timestamp;
}
