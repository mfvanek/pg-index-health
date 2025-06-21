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

public class CreateProceduresStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute() {
        return List.of(
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
