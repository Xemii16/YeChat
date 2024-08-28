package com.yechat.notification.user;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

public interface UserStatusService {

    /**
     * Find all user statuses by existing chats for the authenticated user
     * @param currentUser
     * @return all user statuses
     */
    @PreAuthorize("@verifyJwt.apply(#currentUser)")
    Flux<UserStatus> findStatusesByCurrentUser(Jwt currentUser);

}
