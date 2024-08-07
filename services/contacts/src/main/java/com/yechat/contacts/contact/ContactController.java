package com.yechat.contacts.contact;

import com.yechat.contacts.contact.request.ContactRequest;
import com.yechat.contacts.contact.response.ContactResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
@Profile("!testing")
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public Mono<ContactResponse> createUser(@RequestBody @Valid ContactRequest request, @AuthenticationPrincipal Jwt user) {
        return contactService.createContact(request, user);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<ContactResponse> getContacts(@AuthenticationPrincipal Jwt user) {
        return contactService.getContacts(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteContact(@PathVariable Integer id, @AuthenticationPrincipal Jwt user) {
        return contactService
                .deleteContact(id, user);
    }
}
