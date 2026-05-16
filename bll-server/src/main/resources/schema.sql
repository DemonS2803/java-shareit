drop table if exists comments;
drop table if exists bookings;
drop table if exists items;
drop table if exists users;

create table if not exists users (
    id bigserial primary key not null,
    name varchar(255),
    surname varchar(255),
    email varchar(512) unique not null
);

create table if not exists items (
    id bigserial primary key not null,
    name varchar(255) not null,
    description varchar(255),
    available boolean not null,
    owner_id bigint references users(id)
);

create table if not exists bookings (
    id bigserial primary key not null,
    item_id bigint references items(id),
    user_id bigint references users(id),
    state smallint not null default 0,
    approved boolean default false,
    from_time timestamp,
    to_time timestamp
);

create table if not exists comments (
    id bigserial primary key not null,
    user_id bigint references users(id),
    item_id bigint references items(id),
    text text not null,
    created timestamp default current_timestamp
);
