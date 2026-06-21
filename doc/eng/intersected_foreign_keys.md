# Check for intersected (overlapping) foreign keys in tables

## How intersected foreign keys appear and why you sometimes need to get rid of them

Foreign keys can be created on several attributes (columns) of the target table.
When the data structure changes, a new constraint in the target table and a new foreign key in the referencing table may be required.
If the obsolete foreign key remains, this increases cognitive complexity and
makes maintaining and developing the data structure harder going forward.

## SQL query

- [intersected_foreign_keys.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/intersected_foreign_keys.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Partitioned tables are supported.
The check is performed on the partitioned (parent) table itself. Individual partitions (children) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

create table if not exists demo.clients(
    id          bigint       not null primary key generated always as identity,
    last_name   varchar(255) not null,
    first_name  varchar(255) not null,
    middle_name varchar(255),
    email       varchar(200) not null,
    phone       varchar(50)  not null
);

create unique index if not exists i_clients_email_phone on demo.clients (email, phone);

create table if not exists demo.client_preferences(
    id              bigint       not null generated always as identity,
    email           varchar(200) not null,
    phone           varchar(50)  not null,
    call_time_start timetz       not null,
    call_time_end   timetz       not null
);

alter table if exists demo.client_preferences
    add constraint c_client_preferences_email_phone_fk
        foreign key (email, phone) references demo.clients (email, phone);

alter table if exists demo.client_preferences
    add constraint c_client_preferences_phone_email_fk
        foreign key (phone, email) references demo.clients (phone, email);


create table if not exists demo.dict(
    ref_type    int not null primary key,
    ref_value   varchar(64),
    description text
);

create table if not exists demo.partitioned_table(
    ref_value     varchar(64)              not null,
    ref_type      bigserial                not null references demo.dict (ref_type),
    creation_date timestamp with time zone not null,
    entity_id     varchar(64)              not null,
    deleted       boolean                  not null,
    primary key (ref_value, ref_type, creation_date, entity_id)
) partition by range (creation_date);

create table if not exists demo.partitioned_table_default
    partition of demo.partitioned_table default;

create unique index if not exists idx_dict_ref_type_ref_value on demo.dict (ref_type, ref_value);

alter table if exists demo.partitioned_table
    add constraint partitioned_table_ref_type_ref_value_fk
        foreign key (ref_type, ref_value) references demo.dict (ref_type, ref_value);
```

## How to fix

Drop the redundant foreign keys.
