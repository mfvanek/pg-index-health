/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.maintenance;

import io.github.mfvanek.pg.connection.HostAware;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.table.TableNameAware;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * A check on database structure on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.5.1
 */
public interface DatabaseCheckOnHost<T extends TableNameAware> extends DiagnosticAware, RawTypeAware<T>, HostAware {

    /**
     * Executes the check in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of deviations from the specified rule
     * @see PgContext
     */
    @Nonnull
    List<T> check(@Nonnull PgContext pgContext);
}
