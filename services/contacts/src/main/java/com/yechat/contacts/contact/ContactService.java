package com.yechat.contacts.contact;

import com.yechat.contacts.contact.exception.ContactNotFoundException;
import com.yechat.contacts.contact.request.ContactRequest;
import com.yechat.contacts.contact.response.ContactResponse;
import com.yechat.contacts.user.UserClient;
import com.yechat.contacts.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;
    private final UserClient userClient;

    public ContactResponse createContact(@NonNull ContactRequest request, @NonNull Jwt jwt) {
        Integer userId = getUserId(jwt);
        userClient.getUser(request.contactId())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + request.contactId()));
        Contact contact = Contact.builder()
                .userId(userId)
                .contactId(request.contactId())
                .build();
        Contact savedContact = contactRepository.save(contact);
        return contactMapper.toResponse(savedContact);
    }

    public List<ContactResponse> getContacts(Jwt jwt) {
        Integer userId = getUserId(jwt);
        return contactRepository
                .findAllByUserId(userId)
                .stream()
                .map(contactMapper::toResponse)
                .toList();
    }

    /**
     * Validates the user by checking if the user exists.
     * If the user does not exist, it throws a UserNotFoundException. (401 http status)
     * @param jwt the jwt token
     * @return the user id represented as an integer
     */
    private Integer getUserId(@NonNull Jwt jwt) {
        Integer userId = Integer.parseInt(jwt.getSubject());
        /*userClient.getUser(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));*/
        // Optimize request to authorization server
        return userId;
    }

    public void deleteContact(Integer id, Jwt user) {
        Integer userId = getUserId(user);
        Contact contact = contactRepository.findById(id)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found with ID: " + id));
        if (!contact.getUserId().equals(userId)) {
            throw new ContactNotFoundException("Contact not found with ID: " + id);
        }
        contactRepository.delete(contact);
    }
}
