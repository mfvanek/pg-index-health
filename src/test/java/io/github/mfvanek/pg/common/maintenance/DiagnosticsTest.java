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

import io.github.mfvanek.pg.utils.Locales;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DiagnosticsTest {

    @Test
    void sqlQueryFileNameShouldBeUnique() {
        final Set<String> fileNames = new HashSet<>();
        for (final Diagnostics diagnostics : Diagnostics.values()) {
            fileNames.add(diagnostics.getSqlQueryFileName());
        }
        assertThat(fileNames).hasSize(Diagnostics.values().length);
    }

    @Test
    void sqlQueryFileNameShouldBeInLowerCase() {
        for (final Diagnostics diagnostics : Diagnostics.values()) {
            final String lower = diagnostics.getSqlQueryFileName().toLowerCase(Locales.DEFAULT);
            assertThat(diagnostics.getSqlQueryFileName()).isEqualTo(lower);
        }
    }

    @Test
    void sqlQueryFileNameShouldHaveSqlExtension() {
        for (final Diagnostics diagnostics : Diagnostics.values()) {
            assertThat(diagnostics.getSqlQueryFileName()).endsWith(".sql");
        }
    }
}
