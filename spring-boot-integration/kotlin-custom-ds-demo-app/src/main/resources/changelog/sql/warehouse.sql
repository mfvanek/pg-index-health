--liquibase formatted sql

--changeset ivan.vakhrushev:2024.12.04:warehouse.table
create table if not exists warehouse
(
    id bigint primary key generated always as identity,
    name text not null
);

comment on table warehouse is 'Information about the warehouses';
comment on column warehouse.id is 'Unique identifier of the warehouse';
comment on column warehouse.name is 'Human readable name of the warehouse';
