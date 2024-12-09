--liquibase formatted sql

--changeset ivan.vakhrushev:2024.12.09:warehouse.table
create table if not exists warehouse
(
    id bigserial primary key,
    name text not null
);

comment on table warehouse is 'Information about the warehouses';
comment on column warehouse.id is 'Unique identifier of the warehouse';
comment on column warehouse.name is 'Human readable name of the warehouse';

--changeset ivan.vakhrushev:2024.12.09:warehouse.initial.data
insert into warehouse (name)
select string_agg(substr(md5(random()::text), 1, 8), '')
from generate_series(1, 400);
