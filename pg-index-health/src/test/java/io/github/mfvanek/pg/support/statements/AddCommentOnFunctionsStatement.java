/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support.statements;

import java.util.List;
import javax.annotation.Nonnull;

public class AddCommentOnFunctionsStatement extends AbstractDbStatement {

    @Override
    protected List<String> getSqlToExecute(@Nonnull final String schemaName) {
        return List.of(String.format("comment on function %1$s.add(a integer, b integer) is 'Sums two given arguments';" +
            "comment on function %1$s.add(a int, b int, c int) is 'Sums three given arguments';", schemaName));
    }
}
