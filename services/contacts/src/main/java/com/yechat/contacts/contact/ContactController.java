package com.yechat.contacts.contact;

import com.yechat.contacts.contact.request.ContactRequest;
import com.yechat.contacts.contact.response.ContactResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ContactResponse> createUser(@RequestBody @Valid ContactRequest request, @AuthenticationPrincipal Jwt user) {
        return ResponseEntity.ok(contactService.createContact(request, user));
    }
}
