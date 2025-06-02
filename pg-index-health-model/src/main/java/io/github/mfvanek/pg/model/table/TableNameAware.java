/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

/**
 * Allows getting table name.
 *
 * @author Ivan Vakhrushev
 * @see io.github.mfvanek.pg.model.index.IndexNameAware
 */
public interface TableNameAware {

    /**
     * Retrieves table name.
     *
     * @return table name
     */
    String getTableName();
}
