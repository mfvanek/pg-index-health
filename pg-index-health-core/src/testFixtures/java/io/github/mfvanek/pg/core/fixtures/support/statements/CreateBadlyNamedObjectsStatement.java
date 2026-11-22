/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import java.util.List;

/**
 * Represents a database statement that creates database objects with non-standard or uppercase names,
 * including names that start with a digit.
 * Also creates badly named table, function and domain that are added to the {@code lo} extension.
 */
public class CreateBadlyNamedObjectsStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateBadlyNamedObjectsStatement}.
     */
    public CreateBadlyNamedObjectsStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            """
                create table if not exists {schemaName}."UpperCaseTable"(
                    "UpperCaseId" int
                );""",
            """
                create table if not exists {schemaName}."bad-table"(
                    "bad-id" serial not null primary key
                );""",
            """
                create table if not exists {schemaName}."bad-table-two"(
                    "bad-ref-id" int not null primary key,
                    description text
                );""",
            """
                alter table if exists {schemaName}."bad-table-two"
                    add constraint "bad-table-two-fk-bad-ref-id"
                    foreign key ("bad-ref-id") references {schemaName}."bad-table" ("bad-id") not valid;""",
            """
                create or replace function {schemaName}."bad-add"(a integer, b integer) returns integer
                    as 'select $1 + $2;'
                    language sql
                    immutable
                    returns null on null input;""",
            """
                create table if not exists {schemaName}."1st_table"(
                    "2nd_id" int not null primary key
                );""",
            """
                create or replace function {schemaName}."3rd_add"(a integer, b integer) returns integer
                    as 'select $1 + $2;'
                    language sql
                    immutable
                    returns null on null input;""",
            // Badly named objects that belong to an extension
            "create extension if not exists lo schema {schemaName};",
            """
                create table if not exists {schemaName}."ext-table"(
                    id int constraint "ext-table-pk" primary key
                );""",
            """
                create or replace function {schemaName}."ext-func"() returns integer
                    as 'select 1;'
                    language sql
                    immutable;""",
            "create domain {schemaName}.\"ext-domain\" as integer constraint \"ext-domain-check\" check (value > 0);",
            "alter extension lo add table {schemaName}.\"ext-table\";",
            "alter extension lo add function {schemaName}.\"ext-func\"();",
            "alter extension lo add domain {schemaName}.\"ext-domain\";"
        );
    }
}
