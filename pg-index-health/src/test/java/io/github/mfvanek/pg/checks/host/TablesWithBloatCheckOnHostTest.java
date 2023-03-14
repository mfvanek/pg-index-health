/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnHost;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

class TablesWithBloatCheckOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<TableWithBloat> check = new TablesWithBloatCheckOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(TableWithBloat.class)
                .hasDiagnostic(Diagnostic.BLOATED_TABLES)
                .hasHost(getHost());
    }
}
