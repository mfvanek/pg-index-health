/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.ConstraintType;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.github.mfvanek.pg.support.AbstractCheckOnHostAssert.assertThat;

public class NotValidConstraintsOnHostTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnHost<Constraint> check = new NotValidConstraintsOnHost(getPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
                .hasType(Constraint.class)
                .hasDiagnostic(Diagnostic.NOT_VALID_CONSTRAINTS)
                .hasHost(getHost());
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withNotValidConstraints().withUniqueConstraintOnSerialColumn(), ctx ->
                assertThat(check)
                        .executing(ctx)
                        .hasSize(2)
                        .containsExactly(
                                Constraint.of(ctx.enrichWithSchema("accounts"), "c_accounts_chk_client_id_not_validated_yet",
                                        ConstraintType.CHECK),
                                Constraint.of(ctx.enrichWithSchema("accounts"), "c_accounts_fk_client_id_not_validated_yet",
                                        ConstraintType.FOREIGN_KEY)
                        )
        );
    }
}
