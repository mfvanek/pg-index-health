/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support.statements;

import java.util.Objects;
import javax.annotation.Nonnull;

abstract class AbstractDbStatement implements DbStatement {

    protected final String schemaName;

    protected AbstractDbStatement(@Nonnull final String schemaName) {
        this.schemaName = Objects.requireNonNull(schemaName);
    }
}
