package com.yechat.contacts.contact;

import com.yechat.contacts.contact.request.ContactRequest;
import com.yechat.contacts.contact.response.ContactResponse;
import com.yechat.contacts.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
    public ResponseEntity<Flux<ContactResponse>> getContacts(@AuthenticationPrincipal Jwt user) {
        return ResponseEntity.ok(contactService.getContacts(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Mono<Void>> deleteContact(@PathVariable Integer id, @AuthenticationPrincipal Jwt user) {
        contactService.deleteContact(id, user);
        return ResponseEntity.noContent().build();
    }
}
