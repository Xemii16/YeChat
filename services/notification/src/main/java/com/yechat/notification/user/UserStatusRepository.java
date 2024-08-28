package com.yechat.notification.user;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserStatusRepository extends ReactiveCrudRepository<UserStatus, String> {

    Mono<UserStatus> findByUserId(Integer userId);
}
