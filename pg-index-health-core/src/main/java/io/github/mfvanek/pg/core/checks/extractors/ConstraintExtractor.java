/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.extractors;

import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.ConstraintType;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link Constraint} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.0
 */
public final class ConstraintExtractor implements ResultSetExtractor<Constraint> {

    private ConstraintExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Constraint extractData(final ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TableExtractor.TABLE_NAME);
        final String constraintName = resultSet.getString(ForeignKeyExtractor.CONSTRAINT_NAME);
        final String constraintType = resultSet.getString("constraint_type");
        final ConstraintType ct = ConstraintType.valueFrom(constraintType);
        return Constraint.ofType(tableName, constraintName, ct);
    }

    /**
     * Creates {@code ConstraintExtractor} instance.
     *
     * @return {@code ConstraintExtractor} instance.
     */
    public static ResultSetExtractor<Constraint> of() {
        return new ConstraintExtractor();
    }
}
