/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.sequence.SequenceState;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for sequence overflow on a specific host.
 *
 * @author Blohny
 * @since 0.12.0
 */
public class SequenceOverflowCheckOnHost extends AbstractCheckOnHost<SequenceState> {

    public SequenceOverflowCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(SequenceState.class, pgConnection, Diagnostic.SEQUENCE_OVERFLOW);
    }

    /**
     * Returns sequences that are close to overflow in the specified schema.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @return list of sequences close to overflow
     * @see SequenceState
     */
    @Nonnull
    @Override
    protected List<SequenceState> doCheck(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String sequenceName = rs.getString("sequence_name");
            final String dataType = rs.getString("data_type");
            final double remainingPercentage = rs.getDouble("remaining_percentage");
            return SequenceState.of(sequenceName, dataType, remainingPercentage);
        });
    }
}
