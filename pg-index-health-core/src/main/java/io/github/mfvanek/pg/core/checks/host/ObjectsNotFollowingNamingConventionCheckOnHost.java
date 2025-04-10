/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
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
import javax.annotation.Nonnull;

/**
 * Check for objects whose names do not follow naming convention (that have to be enclosed in double-quotes) on a specific host.
 * <p>
 * You should avoid using quoted identifiers.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.postgresql.org/docs/17/sql-syntax-lexical.html#SQL-SYNTAX-IDENTIFIERS">PostgreSQL Naming Convention</a>
 * @since 0.14.6
 */
public class ObjectsNotFollowingNamingConventionCheckOnHost extends AbstractCheckOnHost<AnyObject> {

    public ObjectsNotFollowingNamingConventionCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(AnyObject.class, pgConnection, Diagnostic.OBJECTS_NOT_FOLLOWING_NAMING_CONVENTION);
    }

    /**
     * Returns objects whose names do not follow naming convention in the specified schema.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @return list of objects whose names do not follow naming convention
     */
    @Nonnull
    @Override
    protected List<AnyObject> doCheck(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, AnyObjectExtractor.of());
    }
}
