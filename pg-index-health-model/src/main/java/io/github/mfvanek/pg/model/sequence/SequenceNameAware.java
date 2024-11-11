/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.sequence;

import javax.annotation.Nonnull;

/**
 * Represents an object that is aware of a database sequence name.
 * Classes implementing this interface provide a method to retrieve the name of a sequence.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.3
 */
public interface SequenceNameAware {

    /**
     * Retrieves the name of the sequence associated with the implementing object.
     *
     * @return the name of the sequence, never null
     */
    @Nonnull
    String getSequenceName();
}
