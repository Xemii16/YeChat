package com.yechat.contacts.contact;

import com.yechat.contacts.contact.request.ContactRequest;
import com.yechat.contacts.contact.response.ContactResponse;
import com.yechat.contacts.user.UserClient;
import com.yechat.contacts.user.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final UserClient userClient;

    public ContactResponse createContact(@NonNull ContactRequest request, @NonNull Jwt jwt) {
        Integer userId = Integer.parseInt(jwt.getSubject());
        userClient.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
        userClient.getUser(request.contactId())
                .orElseThrow(() -> new UserNotFoundException("Contact not found with ID: " + request.contactId()));
        Contact contact = Contact.builder()
                .userId(userId)
                .contactId(request.contactId())
                .build();
        Contact savedContact = contactRepository.save(contact);
        return contactMapper.toResponse(savedContact);
    }
}
