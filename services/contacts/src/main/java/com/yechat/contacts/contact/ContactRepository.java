package com.yechat.contacts.contact;

import org.springframework.context.annotation.Profile;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Profile("!testing")
public interface ContactRepository extends ReactiveCrudRepository<Contact, Integer> {

    Flux<Contact> findAllByUserId(Integer userId);

    Mono<Contact> findByContactId(Integer contactId);
}
