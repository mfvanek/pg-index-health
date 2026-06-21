# Check for duplicated foreign keys in tables

## How they appear and why you should get rid of duplicated foreign keys

Foreign keys can be created on several attributes of the target table — a reference to a constraint on several columns.
In this case, a mistake is possible — creating another foreign key with the same attributes.
Duplication of entities increases cognitive complexity and makes maintaining and developing the data schema harder in the future.

## SQL query

- [duplicated_foreign_keys.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/duplicated_foreign_keys.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

-- For ordinary (non-partitioned) tables

create table if not exists demo.orders(
    id bigint primary key generated always as identity,
    user_id bigint not null,
    shop_id bigint not null,
    status int not null,
    created_at timestamptz not null default current_timestamp
);

create table if not exists demo.order_item(
    id bigint primary key generated always as identity,
    order_id bigint not null references demo.orders (id),
    price decimal(22, 2) not null default 0,
    amount int not null default 0,
    sku varchar(255) not null,
    warehouse_id int
);

alter table if exists demo.order_item
    add constraint c_order_item_fk_order_id_duplicate
        foreign key (order_id) references demo.orders (id);

-- For partitioned tables

create table if not exists demo.orders_partitioned(
    id         bigint not null generated always as identity,
    user_id    bigint      not null,
    shop_id    bigint      not null,
    status     int         not null,
    created_at timestamptz not null default current_timestamp,
    primary key (id, created_at)
) partition by range (created_at);

create table if not exists demo.orders_default
    partition of demo.orders_partitioned default;

create table if not exists demo.order_item_partitioned(
    id           bigint generated always as identity,
    order_id     bigint         not null,
    created_at   timestamptz    not null,
    price        decimal(22, 2) not null default 0,
    amount       int            not null default 0,
    sku          varchar(255)   not null,
    warehouse_id int,
    primary key (id, created_at),
    constraint fk_order_item_order_id foreign key (order_id, created_at)
      references demo.orders_partitioned (id, created_at)
) partition by range (created_at);

alter table if exists demo.order_item_partitioned
    add constraint c_order_item_partitioned_fk_order_id_created_at_duplicate
        foreign key (order_id, created_at) references demo.orders_partitioned (id, created_at);

create table if not exists demo.order_item_default
    partition of demo.order_item_partitioned default;
```

## How to fix

Choose the foreign key you want to keep and drop the duplicate.
