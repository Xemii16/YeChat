create table contacts
(
    id         serial  not null primary key,
    user_id    integer not null,
    contact_id integer not null
);