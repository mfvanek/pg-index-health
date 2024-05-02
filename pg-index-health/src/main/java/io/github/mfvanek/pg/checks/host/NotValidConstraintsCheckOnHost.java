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

/**
 * Check for not valid constraints on a specific host.
 *
 * @author Blohny
 * @since 0.10.4
 */
public class NotValidConstraintsCheckOnHost extends AbstractCheckOnHost<Constraint> {

    public NotValidConstraintsCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(Constraint.class, pgConnection, Diagnostic.NOT_VALID_CONSTRAINTS);
    }

    /**
     * Returns not valid constraints in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of not valid constraints
     * @see Constraint
     */
    @Nonnull
    @Override
    public List<Constraint> check(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String tableName = rs.getString(TABLE_NAME);
            final String constraintName = rs.getString(CONSTRAINT_NAME);
            final String constraintType = rs.getString("constraint_type");
            final ConstraintType ct = ConstraintType.valueFrom(constraintType);
            return Constraint.ofType(tableName, constraintName, ct);
        });
    }
}
