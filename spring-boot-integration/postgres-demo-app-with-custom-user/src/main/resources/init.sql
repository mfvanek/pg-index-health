/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

create schema if not exists main_schema authorization main_user;
create schema if not exists additional_schema authorization pg_database_owner;

create user custom_user with nosuperuser nocreatedb nocreaterole password 'customUserPassword' connection limit 10;
grant usage on schema main_schema to custom_user;

create table if not exists additional_schema.additional_table (
    id bigserial primary key,
    name text not null
);
