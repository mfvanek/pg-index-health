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

import java.sql.SQLException;
import java.sql.Statement;
import javax.annotation.Nonnull;

public class AddBlankCommentOnTablesStatement extends AbstractDbStatement {

    public AddBlankCommentOnTablesStatement(@Nonnull final String schemaName) {
        super(schemaName);
    }

    @Override
    public void execute(@Nonnull final Statement statement) throws SQLException {
        statement.execute(String.format("comment on table %1$s.clients is '   ';" +
                "comment on table %1$s.accounts is '';", schemaName));
    }
}
