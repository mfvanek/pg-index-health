/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.extractors;

import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.model.sequence.SequenceState;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link SequenceState} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.0
 */
public final class SequenceStateExtractor implements ResultSetExtractor<SequenceState> {

    private SequenceStateExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceState extractData(final ResultSet resultSet) throws SQLException {
        final String sequenceName = resultSet.getString("sequence_name");
        final String dataType = resultSet.getString("data_type");
        final double remainingPercentage = resultSet.getDouble("remaining_percentage");
        return SequenceState.of(sequenceName, dataType, remainingPercentage);
    }

    /**
     * Creates {@code SequenceStateExtractor} instance.
     *
     * @return {@code SequenceStateExtractor} instance.
     */
    public static ResultSetExtractor<SequenceState> of() {
        return new SequenceStateExtractor();
    }
}
