package com.yechat.contacts.user;

import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;


public interface UserClient {

    Mono<UserResponse> getUser(@PathVariable Integer id);
}
