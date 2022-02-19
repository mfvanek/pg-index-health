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

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiagnosticsTest {

    @Test
    void sqlQueryFileNameShouldBeUnique() {
        final Set<String> fileNames = new HashSet<>();
        for (Diagnostics diagnostics : Diagnostics.values()) {
            fileNames.add(diagnostics.getSqlQueryFileName());
        }
        assertThat(fileNames, hasSize(Diagnostics.values().length));
    }

    @Test
    void sqlQueryFileNameShouldBeInLowerCase() {
        for (Diagnostics diagnostics : Diagnostics.values()) {
            final String lower = diagnostics.getSqlQueryFileName().toLowerCase();
            assertEquals(lower, diagnostics.getSqlQueryFileName());
        }
    }

    @Test
    void sqlQueryFileNameShouldHaveSqlExtension() {
        for (Diagnostics diagnostics : Diagnostics.values()) {
            assertTrue(diagnostics.getSqlQueryFileName().endsWith(".sql"));
        }
    }
}
