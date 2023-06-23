drop table if exists customer;

create table if not exists customer (
    id varchar(255) primary key,
    name varchar(255) not null,
    order_count int default 0,
    member_since timestamp with time zone not null,
    last_purchased timestamp with time zone default null,
    version int not null
);