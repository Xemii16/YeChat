package com.yechat.notification.user;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("user-status")
public class UserStatus {
    @Id
    private String id;
    private Integer userId;
    private Status status;

    public enum Status {
        ONLINE,
        OFFLINE
    }
}
