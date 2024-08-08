package com.yechat.chats.user;

import com.yechat.chats.user.response.UserResponse;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

public interface UserClient {

    Mono<UserResponse> getUser(@PathVariable Integer id);
}
