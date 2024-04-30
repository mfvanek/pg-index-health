/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.ConstraintType;

import java.util.List;
import javax.annotation.Nonnull;

public class NotValidConstraintsOnHost extends AbstractCheckOnHost<Constraint> {

    public NotValidConstraintsOnHost(@Nonnull final PgConnection pgConnection) {
        super(Constraint.class, pgConnection, Diagnostic.NOT_VALID_CONSTRAINTS);
    }

    @Nonnull
    @Override
    public List<Constraint> check(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final String constraintName = rs.getString("constraint_name");
            final String constraintType = rs.getString("constraint_type");
            final ConstraintType ct = constraintType.equals("c") ? ConstraintType.CHECK : ConstraintType.FOREIGN_KEY;
            return Constraint.of(tableName, constraintName, ct);
        });
    }
}
