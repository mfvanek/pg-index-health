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
import io.github.mfvanek.pg.model.function.StoredFunction;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link StoredFunction} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.30.0
 */
public final class StoredFunctionExtractor implements ResultSetExtractor<StoredFunction> {

    private StoredFunctionExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StoredFunction extractData(final ResultSet resultSet) throws SQLException {
        final String functionName = resultSet.getString("function_name");
        final String functionSignature = resultSet.getString("function_signature");
        return StoredFunction.of(functionName, functionSignature);
    }

    /**
     * Creates {@code StoredFunctionExtractor} instance.
     *
     * @return a {@code StoredFunctionExtractor} instance.
     */
    public static ResultSetExtractor<StoredFunction> of() {
        return new StoredFunctionExtractor();
    }
}
