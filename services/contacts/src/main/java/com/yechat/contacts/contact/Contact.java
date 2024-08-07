package com.yechat.contacts.contact;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "contacts")
public class Contact {

    @Id
    private Integer id;
    @Column("user_id")
    private Integer userId;
    @Column("contact_id")
    private Integer contactId;
}
