/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

/**
 * Allows getting index name.
 *
 * @author Ivan Vakhrushev
 * @see io.github.mfvanek.pg.model.table.TableNameAware
 */
public interface IndexNameAware {

    /**
     * Constant representing the field name for index names in a database entity.
     */
    String INDEX_NAME_FIELD = "indexName";

    /**
     * Retrieves index name.
     *
     * @return index name
     */
    String getIndexName();
}
