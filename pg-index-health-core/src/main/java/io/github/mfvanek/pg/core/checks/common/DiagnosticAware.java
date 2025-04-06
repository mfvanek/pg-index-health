/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.common;

import javax.annotation.Nonnull;

/**
 * Allows getting information about rule related to the check.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
public interface DiagnosticAware {

    /**
     * Retrieves the diagnostic - a rule related to the check.
     *
     * @return diagnostic
     * @see Diagnostic
     */
    @Nonnull
    Diagnostic getDiagnostic();
}
