/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import io.github.mfvanek.pg.checks.predicates.FilterTablesByNamePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.ConstraintType;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.support.ExecuteUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

public class NotValidConstraintsCheckOnClusterTest extends DatabaseAwareTestBase {

    private final DatabaseCheckOnCluster<Constraint> check = new NotValidConstraintsCheckOnCluster(getHaPgConnection());

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(Constraint.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.NOT_VALID_CONSTRAINTS);
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void testConstraintsOfTypeCAndF(String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withNotValidConstraints().withUniqueConstraintOnSerialColumn(), ctx -> {
            assertThat(check.check(ctx))
                    .hasSize(2)
                    .containsExactly(
                            Constraint.of(ctx.enrichWithSchema("accounts"), "c_accounts_chk_client_id_not_validated_yet", ConstraintType.CHECK),
                            Constraint.of(ctx.enrichWithSchema("accounts"), "c_accounts_fk_client_id_not_validated_yet", ConstraintType.FOREIGN_KEY)
                    );

            ExecuteUtils.executeOnDatabase(DatabaseAwareTestBase.getDataSource(), statement -> {
                statement.execute(String.format("alter table %1$s.accounts validate constraint c_accounts_fk_client_id_not_validated_yet;", schemaName));
                statement.execute(String.format("alter table %1$s.accounts validate constraint c_accounts_chk_client_id_not_validated_yet;", schemaName));
            });

            assertThat(check.check(ctx))
                    .isEmpty();

            assertThat(check.check(ctx, FilterTablesByNamePredicate.of(ctx.enrichWithSchema("accounts"))))
                    .isEmpty();
        });
    }
}
