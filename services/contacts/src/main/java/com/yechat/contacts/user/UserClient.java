package com.yechat.contacts.user;

import com.yechat.contacts.user.exception.UserNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;


public interface UserClient {

    Optional<UserResponse> getUser(@PathVariable Integer id) throws UserNotFoundException;
}
