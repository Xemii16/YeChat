package com.yechat.chats.chat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponse {
    @JsonProperty("chat_id")
    private UUID chatId;
    @JsonProperty("receiver_id")
    private Integer receiverId;
}
