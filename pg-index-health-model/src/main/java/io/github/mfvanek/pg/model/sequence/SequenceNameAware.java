/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.sequence;

/**
 * Represents an object that is aware of a database sequence name.
 * Classes implementing this interface provide a method to retrieve the name of a sequence.
 *
 * @author Ivan Vakhrushev
 * @since 0.13.3
 */
public interface SequenceNameAware {

    /**
     * Represents the field name for storing the name of the sequence.
     */
    String SEQUENCE_NAME_FIELD = "sequenceName";

    /**
     * Retrieves the name of the sequence associated with the implementing object.
     *
     * @return the name of the sequence, never null
     */
    String getSequenceName();
}
