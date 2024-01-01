/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import javax.annotation.Nonnull;

/**
 * Allows getting table name.
 *
 * @author Ivan Vakhrushev
 * @see io.github.mfvanek.pg.model.index.IndexNameAware
 */
public interface TableNameAware {

    /**
     * Gets table name.
     *
     * @return table name
     */
    @Nonnull
    String getTableName();
}
