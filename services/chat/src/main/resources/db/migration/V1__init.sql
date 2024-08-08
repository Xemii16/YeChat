create table chats
(
    id          serial      not null primary key,
    chat_id     varchar(36) not null,
    sender_id   integer     not null,
    receiver_id integer     not null
);