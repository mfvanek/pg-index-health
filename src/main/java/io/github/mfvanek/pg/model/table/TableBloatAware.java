/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.table;

import io.github.mfvanek.pg.model.BloatAware;

/**
 * Allows getting information about table bloat.
 *
 * @author Ivan Vakhrushev
 * @since 0.5.1
 * @see BloatAware
 */
public interface TableBloatAware extends BloatAware, TableSizeAware {
}
