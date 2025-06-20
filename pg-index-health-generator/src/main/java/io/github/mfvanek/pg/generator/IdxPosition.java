/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

/**
 * Possible positions of "idx" in the generated index name.
 *
 * @author Ivan Vakhrushev
 * @since 0.5.0
 */
public enum IdxPosition {

    /**
     * Do not add "idx" to the index name.
     */
    NONE,
    /**
     * Add "idx" to the beginning of the index name.
     */
    PREFIX,
    /**
     * Add "idx" to the end of the index name.
     */
    SUFFIX
}
