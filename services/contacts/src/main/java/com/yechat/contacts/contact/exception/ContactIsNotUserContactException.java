package com.yechat.contacts.contact.exception;

public class ContactIsNotUserContactException extends ContactException {
    public ContactIsNotUserContactException(Integer id) {
        super("Contact is not user contact with ID: " + id);
    }
}
