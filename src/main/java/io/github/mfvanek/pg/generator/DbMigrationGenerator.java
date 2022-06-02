/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import io.github.mfvanek.pg.model.index.ForeignKey;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Database migrations generator.
 *
 * @author Ivan Vahrushev
 * @since 0.5.0
 */
public interface DbMigrationGenerator {

    @Nonnull
    String generate(@Nonnull List<ForeignKey> foreignKeys, @Nonnull GeneratingOptions options);
}
