package com.yechat.contacts.contact.exception;

public class ContactNotExistException extends ContactException {
    public ContactNotExistException(Integer id) {
        super("Contact not found with ID: " + id);
    }
}
