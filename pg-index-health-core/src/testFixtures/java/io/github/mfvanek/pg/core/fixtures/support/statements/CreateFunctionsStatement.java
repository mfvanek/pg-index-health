/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import java.util.List;

public class CreateFunctionsStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
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
                    returns null on null input;"""
        );
    }
}
