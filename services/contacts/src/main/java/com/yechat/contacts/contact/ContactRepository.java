package com.yechat.contacts.contact;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
@Profile("!testing")
public interface ContactRepository extends ReactiveCrudRepository<Contact, Integer> {

    Flux<Contact> findAllByUserId(Integer userId);
}
