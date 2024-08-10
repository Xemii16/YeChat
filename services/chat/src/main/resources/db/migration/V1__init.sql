create table chats
(
    id          serial  not null primary key,
    chat_id     uuid    not null,
    sender_id   integer not null,
    receiver_id integer not null
);