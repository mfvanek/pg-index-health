/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.fixtures.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.core.fixtures.support.ExecuteUtils;
import io.github.mfvanek.pg.health.checks.common.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.ConstraintType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static io.github.mfvanek.pg.health.support.AbstractCheckOnClusterAssert.assertThat;

class NotValidConstraintsCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<Constraint> check = new NotValidConstraintsCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check)
            .hasType(Constraint.class)
            .hasDiagnostic(Diagnostic.NOT_VALID_CONSTRAINTS)
            .isStatic();
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withNotValidConstraints().withUniqueConstraintOnSerialColumn(), ctx -> {
            final List<Constraint> notValidConstraints = check.check(ctx);
            Assertions.assertThat(notValidConstraints)
                .hasSize(2)
                .containsExactly(
                    Constraint.ofType(ctx.enrichWithSchema("accounts"), "c_accounts_chk_client_id_not_validated_yet", ConstraintType.CHECK),
                    Constraint.ofType(ctx.enrichWithSchema("accounts"), "c_accounts_fk_client_id_not_validated_yet", ConstraintType.FOREIGN_KEY));

            assertThat(check)
                .executing(ctx, SkipTablesByNamePredicate.ofName(ctx, "accounts"))
                .isEmpty();

            ExecuteUtils.executeOnDatabase(getDataSource(), statement -> {
                for (final Constraint constraint : notValidConstraints) {
                    statement.execute(constraint.getValidateSql());
                }
            });

            assertThat(check)
                .executing(ctx)
                .isEmpty();
        });
    }
}
