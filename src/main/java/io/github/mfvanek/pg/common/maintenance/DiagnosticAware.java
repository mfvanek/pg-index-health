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

import javax.annotation.Nonnull;

/**
 * Allows getting information about rule related to the check.
 *
 * @author Ivan Vahrushev
 * @since 0.5.1
 */
public interface DiagnosticAware {

    /**
     * Gets the diagnostic - a rule related to the check.
     *
     * @return diagnostic
     * @see Diagnostic
     */
    @Nonnull
    Diagnostic getDiagnostic();
}
