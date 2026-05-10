/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.core.checks.extractors.AnyObjectExtractor;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.AnyObject;

import java.util.List;

/**
 * Check for objects whose names contain uppercase letters on a specific host.
 * <p>
 * Prefer names_like_this over NamesLikeThis.
 * PostgreSQL folds unquoted identifiers to lowercase, so using uppercase forces you to always quote the identifier.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_upper_case_table_or_column_names">PostgreSQL Wiki: Don't use upper case table or column names</a>
 * @since 0.41.0
 */
public class ObjectsWithUpperCaseNamesCheckOnHost extends AbstractCheckOnHost<AnyObject> {

    /**
     * Constructs a new instance of {@code ObjectsWithUpperCaseNamesCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public ObjectsWithUpperCaseNamesCheckOnHost(final PgConnection pgConnection) {
        super(AnyObject.class, pgConnection, Diagnostic.OBJECTS_WITH_UPPER_CASE_NAMES);
    }

    /**
     * Returns objects whose names contain uppercase letters in the specified schema.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @return list of objects whose names contain uppercase letters
     */
    @Override
    protected List<AnyObject> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, AnyObjectExtractor.of());
    }
}
