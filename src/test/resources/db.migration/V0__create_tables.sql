create table bid
(
    id          bigint  not null auto_increment,
    amount      integer not null,
    datetime    varchar(255),
    customer_id bigint,
    item_id     bigint,
    primary key (id)
) engine = MyISAM;

create table customer
(
    id       bigint  not null auto_increment,
    balance  integer not null,
    password varchar(255),
    username varchar(255),
    primary key (id)
) engine = MyISAM;

create table customer_items_for_sale
(
    customer_id       bigint not null,
    items_for_sale_id bigint not null
) engine = MyISAM;

create table customer_items_owned
(
    customer_id    bigint not null,
    items_owned_id bigint not null
) engine = MyISAM;

create table item
(
    id             bigint  not null auto_increment,
    description    varchar(255),
    last_bid       integer not null,
    name           varchar(255),
    photo_url      varchar(255),
    purchase_price integer not null,
    seller         varchar(255),
    starting_price integer not null,
    buyer_id       bigint,
    primary key (id)
) engine = MyISAM;

create table item_bids
(
    item_id bigint not null,
    bids_id bigint not null
) engine = MyISAM