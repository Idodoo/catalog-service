create sequence product_id_seq start with 1 increment by 20;

create table products
(
    id bigint default nextval('product_id_seq') not null,
    code        text not null unique,
    name        text not null,
    description text,
    image_url   text,
    price       numeric not null,
    primary key (id)
);
