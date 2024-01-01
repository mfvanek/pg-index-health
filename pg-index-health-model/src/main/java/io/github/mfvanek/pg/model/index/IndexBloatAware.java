/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.BloatAware;

/**
 * Allows getting information about index bloat.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 * @see BloatAware
 */
public interface IndexBloatAware extends BloatAware, IndexSizeAware {
}
