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

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.AnyObject;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for objects whose names have a length of {@code max_identifier_length} (usually it is 63) on a specific host.
 * <p>
 * The problem is that Postgres silently truncates such long names.
 * For example, if you have a migration where you are trying to create two objects with very long names
 * that start the same way (such as an index or constraint) and you use the "if not exists" statement,
 * you might end up with only one object in the database instead of two.
 * <p>
 * If there is an object with a name of maximum length in the database, then an overflow may have occurred.
 * It is advisable to avoid such situations and use shorter names.
 *
 * @author Ivan Vahrushev
 * @see <a href="https://www.postgresql.org/docs/current/limits.html">PostgreSQL Limits</a>
 * @since 0.13.2
 */
public class PossibleObjectNameOverflowCheckOnHost extends AbstractCheckOnHost<AnyObject> {

    public PossibleObjectNameOverflowCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(AnyObject.class, pgConnection, Diagnostic.POSSIBLE_OBJECT_NAME_OVERFLOW);
    }

    /**
     * Returns objects whose names have a length of {@code max_identifier_length} in the specified schema.
     *
     * @param pgContext check's context with the specified schema; must not be null
     * @return list of objects whose names have a length of {@code max_identifier_length}
     */
    @Nonnull
    @Override
    protected List<AnyObject> doCheck(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final String objectName = rs.getString("object_name");
            final String objectType = rs.getString("object_type");
            return AnyObject.ofRaw(objectName, objectType);
        });
    }
}
