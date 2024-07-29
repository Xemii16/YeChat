package com.yechat.contacts.contact.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactResponse {
    private Integer id;
    @JsonProperty("contact_id")
    private Integer contactId;
}
