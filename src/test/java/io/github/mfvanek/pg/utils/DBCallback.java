/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import javax.annotation.Nonnull;
import java.sql.SQLException;
import java.sql.Statement;

@FunctionalInterface
interface DBCallback {

    void execute(@Nonnull final Statement statement) throws SQLException;
}
