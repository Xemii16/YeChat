package com.yechat.contacts.contact;

import com.yechat.contacts.contact.response.ContactResponse;
import org.springframework.stereotype.Service;

@Service
public class ContactMapper {

    public ContactResponse toResponse(Contact contact) {
        return ContactResponse.builder()
                .id(contact.getId())
                .contactId(contact.getContactId())
                .build();
    }
}
