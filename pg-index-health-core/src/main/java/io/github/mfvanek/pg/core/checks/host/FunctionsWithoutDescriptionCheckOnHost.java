/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.function.StoredFunction;

import java.util.List;

/**
 * Check for procedures/functions without description on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.7.0
 */
public class FunctionsWithoutDescriptionCheckOnHost extends AbstractCheckOnHost<StoredFunction> {

    public FunctionsWithoutDescriptionCheckOnHost(final PgConnection pgConnection) {
        super(StoredFunction.class, pgConnection, Diagnostic.FUNCTIONS_WITHOUT_DESCRIPTION);
    }

    /**
     * Returns procedures/functions without description (comment) in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of procedures/functions without description
     */
    @Override
    protected List<StoredFunction> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String functionName = rs.getString("function_name");
            final String functionSignature = rs.getString("function_signature");
            return StoredFunction.of(functionName, functionSignature);
        });
    }
}
