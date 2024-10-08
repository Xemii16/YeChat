package com.yechat.contacts.contact;

import com.yechat.contacts.contact.exception.ContactException;
import com.yechat.contacts.contact.exception.ContactIsNotUserContactException;
import com.yechat.contacts.contact.exception.ContactNotExistException;
import com.yechat.contacts.contact.request.ContactRequest;
import com.yechat.contacts.contact.response.ContactResponse;
import com.yechat.contacts.user.UserClient;
import com.yechat.contacts.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Profile("!testing")
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final UserClient userClient;

    public Mono<ContactResponse> createContact(@NonNull ContactRequest request, @NonNull Jwt jwt) {
        Integer userId = getUserId(jwt);
        return userClient.getUser(request.contactId())
                .onErrorMap(this::transformException)
                .flatMap(userResponse -> {
                    Contact contact = Contact.builder()
                            .userId(userId)
                            .contactId(request.contactId())
                            .build();
                    return contactRepository.save(contact)
                            .map(contactMapper::toResponse);
                });
    }

    public Flux<ContactResponse> getContacts(Jwt jwt) {
        Integer userId = getUserId(jwt);
        return contactRepository
                .findAllByUserId(userId)
                .map(contactMapper::toResponse);
    }

    private Integer getUserId(@NonNull Jwt jwt) {
        return Integer.parseInt(jwt.getSubject());
    }

    public Mono<Void> deleteContact(Integer contactId, Jwt user) {
        Integer userId = getUserId(user);
        return contactRepository.findByContactId(contactId)
                .switchIfEmpty(Mono.error(new ContactNotExistException(contactId)))
                .flatMap(contact -> {
                    if (contact != null && !contact.getUserId().equals(userId)) {
                        return Mono.error(new ContactIsNotUserContactException(contactId));
                    }
                    Assert.notNull(contact, "Contact is null with ID: " + contactId);
                    return contactRepository.delete(contact);
                })
                .onErrorMap(this::transformException);
    }

    @NonNull
    private <R> ResponseEntity<R> toEntity(@NonNull R response, @NonNull HttpStatus status) {
        return ResponseEntity
                .status(status)
                .body(response);
    }

    @NonNull
    private Throwable transformException(@NonNull Throwable rawException) {
        if (rawException instanceof UserException exception) {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
        if (rawException instanceof ContactException exception) {
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
        return rawException;
    }
}
