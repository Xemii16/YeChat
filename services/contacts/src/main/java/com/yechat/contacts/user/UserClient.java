package com.yechat.contacts.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@FeignClient(
        name = "user-service"
)
public interface UserClient {

    @GetMapping("/api/v1/users/{id}")
    Optional<UserResponse> getUser(@PathVariable Integer id);
}
