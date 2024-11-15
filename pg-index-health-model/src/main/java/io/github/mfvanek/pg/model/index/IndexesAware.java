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

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Represents an entity that is aware of and can provide a list of index-related information.
 * Classes implementing this interface should provide a list of objects that implement {@link IndexNameAware}.
 *
 * @author Ivan Vakhrushev
 * @see IndexNameAware
 * @since 0.13.3
 */
public interface IndexesAware {

    /**
     * Retrieves a list of objects that are aware of their index names.
     *
     * @return a non-null list of {@link IndexNameAware} objects. The list may be empty if no indexes are available.
     */
    @Nonnull
    List<? extends IndexNameAware> getIndexes();
}
