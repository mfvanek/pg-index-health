/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import java.util.List;

/**
 * Represents an entity that is aware of and can provide a list of indexes.
 * Classes implementing this interface should return a list of objects that extend {@link Index}.
 *
 * @author Ivan Vakhrushev
 * @see Index
 * @since 0.13.3
 */
public interface IndexesAware {

    /**
     * Retrieves a list of indexes associated with this entity.
     *
     * @return a non-null list of {@link Index} or its subclasses. The list may be empty if no indexes are available.
     */
    List<Index> getIndexes();
}
