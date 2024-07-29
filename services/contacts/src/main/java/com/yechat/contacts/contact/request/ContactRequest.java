package com.yechat.contacts.contact.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ContactRequest(
        @NotNull(message = "Contact ID is required")
        @Positive(message = "Contact ID must be positive")
        Integer contactId
) {
}
