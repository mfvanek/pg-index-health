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
import io.github.mfvanek.pg.model.dbobject.AnyObject;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A mapper from raw data to {@link AnyObject} model.
 *
 * @author Ivan Vakhrushev
 * @since 0.14.6
 */
public final class AnyObjectExtractor implements ResultSetExtractor<AnyObject> {

    public static final String OBJECT_NAME = "object_name";
    public static final String OBJECT_TYPE = "object_type";

    private AnyObjectExtractor() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AnyObject extractData(final ResultSet resultSet) throws SQLException {
        final String objectName = resultSet.getString(OBJECT_NAME);
        final String objectType = resultSet.getString(OBJECT_TYPE);
        return AnyObject.ofRaw(objectName, objectType);
    }

    /**
     * Creates {@code AnyObjectExtractor} instance.
     *
     * @return {@code AnyObjectExtractor} instance
     */
    public static ResultSetExtractor<AnyObject> of() {
        return new AnyObjectExtractor();
    }
}
