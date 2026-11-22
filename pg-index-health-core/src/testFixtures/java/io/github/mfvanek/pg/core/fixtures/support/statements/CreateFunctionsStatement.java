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
 * Represents a database statement that creates stored functions and procedures in the database.
 * Also installs the {@code lo} extension, whose functions have no description but belong to the extension.
 */
public class CreateFunctionsStatement extends AbstractDbStatement {

    /**
     * Constructs a new instance of {@code CreateFunctionsStatement}.
     */
    public CreateFunctionsStatement() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
            "create extension if not exists lo schema {schemaName};",
            """
                create or replace function {schemaName}.add(a integer, b integer) returns integer
                    as 'select $1 + $2;'
                    language sql
                    immutable
                    returns null on null input;""",
            """
                create or replace function {schemaName}.add(a int, b int, c int) returns int
                    as 'select $1 + $2 + $3;'
                    language sql
                    immutable
                    returns null on null input;""",
            """
                create or replace procedure {schemaName}.insert_data(a integer, b integer)
                    language sql
                    as $$
                      insert into {schemaName}.clients values (a);
                      insert into {schemaName}.clients values (b);
                    $$;""",
            """
                create or replace procedure {schemaName}.insert_data(a int, b int, c int)
                    language sql
                    as $$
                      insert into {schemaName}.clients values (a);
                      insert into {schemaName}.clients values (b);
                      insert into {schemaName}.clients values (c);
                    $$;"""
        );
    }
}
