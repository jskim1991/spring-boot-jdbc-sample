drop table if exists customer;

create table if not exists customer (
    customer_id varchar(255) primary key,
    name varchar(255) not null,
    member_since timestamp with time zone not null,
    last_purchased timestamp with time zone default null,
    version int not null
);

drop table if exists orders;

create table if not exists orders (
    order_id varchar(255) primary key,
    total int not null,
    purchased_date timestamp with time zone not null,
    version int not null,
    customer_id varchar(255) not null,
    foreign key (customer_id) references customer (customer_id)
);