/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

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
