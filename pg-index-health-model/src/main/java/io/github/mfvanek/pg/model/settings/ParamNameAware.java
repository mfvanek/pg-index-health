/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.settings;

/**
 * Represents an entity that is aware of its parameter name.
 */
public interface ParamNameAware {

    /**
     * Retrieves the name of the parameter.
     *
     * @return the name of the parameter, never {@code null}
     */
    String getName();
}
