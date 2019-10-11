-- auto-generated definition
create table app_user
(
    user_id   bigint not null
        constraint app_user_pkey
            primary key,
    active    integer,
    email     varchar(255),
    last_name varchar(255),
    name      varchar(255),
    password  varchar(255)
);

-- auto-generated definition
create table user_role
(
    user_id bigint not null
        constraint fkg7fr1r7o0fkk41nfhnjdyqn7b
            references app_user,
    role_id bigint not null
        constraint fkt7e7djp752sqn6w22i6ocqy6q
            references roles,
    constraint user_role_pkey
        primary key (user_id, role_id)
);
