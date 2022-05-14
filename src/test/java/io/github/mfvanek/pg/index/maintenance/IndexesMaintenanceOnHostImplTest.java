/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.index.maintenance;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.connection.PgConnectionImpl;
import io.github.mfvanek.pg.embedded.PostgresDbExtension;
import io.github.mfvanek.pg.embedded.PostgresExtensionFactory;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.ForeignKey;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.index.IndexWithNulls;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.table.Column;
import io.github.mfvanek.pg.utils.DatabaseAwareTestBase;
import io.github.mfvanek.pg.utils.DatabasePopulator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;

public final class IndexesMaintenanceOnHostImplTest extends DatabaseAwareTestBase {

    @RegisterExtension
    static final PostgresDbExtension embeddedPostgres = PostgresExtensionFactory.database();

    private final IndexesMaintenanceOnHost indexesMaintenance;

    IndexesMaintenanceOnHostImplTest() {
        super(embeddedPostgres.getTestDatabase());
        final PgConnection pgConnection = PgConnectionImpl.ofPrimary(embeddedPostgres.getTestDatabase());
        this.indexesMaintenance = new IndexMaintenanceOnHostImpl(pgConnection);
    }

    @Test
    void getInvalidIndexesOnEmptyDatabase() {
        final List<Index> invalidIndexes = indexesMaintenance.getInvalidIndexes();
        assertThat(invalidIndexes)
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getInvalidIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx -> {
            final List<Index> invalidIndexes = indexesMaintenance.getInvalidIndexes(ctx);
            assertThat(invalidIndexes)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getInvalidIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withInvalidIndex(), ctx -> {
            final List<Index> invalidIndexes = indexesMaintenance.getInvalidIndexes(ctx);
            assertThat(invalidIndexes)
                    .isNotNull()
                    .hasSize(1)
                    .containsExactly(Index.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_last_name_first_name")));
        });
    }

    @Test
    void getDuplicatedIndexesOnEmptyDatabase() {
        final List<DuplicatedIndexes> duplicatedIndexes = indexesMaintenance.getDuplicatedIndexes();
        assertThat(duplicatedIndexes)
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx -> {
            final List<DuplicatedIndexes> duplicatedIndexes = indexesMaintenance.getDuplicatedIndexes(ctx);
            assertThat(duplicatedIndexes)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDuplicatedIndex(), ctx -> {
            final List<DuplicatedIndexes> duplicatedIndexes = indexesMaintenance.getDuplicatedIndexes(ctx);
            assertThat(duplicatedIndexes)
                    .isNotNull()
                    .hasSize(1);
            final DuplicatedIndexes entry = duplicatedIndexes.get(0);
            assertThat(entry.getTableName()).isEqualTo(ctx.enrichWithSchema("accounts"));
            assertThat(entry.getIndexNames())
                    .containsExactlyInAnyOrder(
                            ctx.enrichWithSchema("accounts_account_number_key"),
                            ctx.enrichWithSchema("i_accounts_account_number"));
            assertThat(entry.getTotalSize()).isGreaterThanOrEqualTo(16384L);
            assertThat(entry.getDuplicatedIndexes()).hasSize(2);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedHashIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDuplicatedHashIndex(), ctx -> {
            final List<DuplicatedIndexes> duplicatedIndexes = indexesMaintenance.getDuplicatedIndexes(ctx);
            assertThat(duplicatedIndexes)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedIndexesWithDifferentOpclassShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDifferentOpclassIndexes(), ctx -> {
            final List<DuplicatedIndexes> duplicatedIndexes = indexesMaintenance.getDuplicatedIndexes(ctx);
            assertThat(duplicatedIndexes)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getDuplicatedIndexesWithDifferentCollationShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withCustomCollation().withDuplicatedCustomCollationIndex(), ctx -> {
            final List<DuplicatedIndexes> duplicatedIndexes = indexesMaintenance.getDuplicatedIndexes(ctx);
            assertThat(duplicatedIndexes)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @Test
    void getIntersectedIndexesOnEmptyDatabase() {
        final List<DuplicatedIndexes> intersectedIndexes = indexesMaintenance.getIntersectedIndexes();
        assertThat(intersectedIndexes)
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIntersectedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx -> {
            final List<DuplicatedIndexes> intersectedIndexes = indexesMaintenance.getIntersectedIndexes(ctx);
            assertThat(intersectedIndexes)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIntersectedIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withDuplicatedIndex(), ctx -> {
            final List<DuplicatedIndexes> intersectedIndexes = indexesMaintenance.getIntersectedIndexes(ctx);
            assertThat(intersectedIndexes)
                    .isNotNull()
                    .hasSize(2);
            final DuplicatedIndexes firstEntry = intersectedIndexes.get(0);
            final DuplicatedIndexes secondEntry = intersectedIndexes.get(1);
            assertThat(firstEntry.getTotalSize()).isGreaterThanOrEqualTo(114688L);
            assertThat(secondEntry.getTotalSize()).isGreaterThanOrEqualTo(106496L);
            assertThat(firstEntry.getDuplicatedIndexes()).hasSize(2);
            assertThat(secondEntry.getDuplicatedIndexes()).hasSize(2);
            assertThat(firstEntry.getTableName()).isEqualTo(ctx.enrichWithSchema("accounts"));
            assertThat(secondEntry.getTableName()).isEqualTo(ctx.enrichWithSchema("clients"));
            assertThat(firstEntry.getIndexNames())
                    .contains(ctx.enrichWithSchema("i_accounts_account_number_not_deleted"), ctx.enrichWithSchema("i_accounts_number_balance_not_deleted"));
            assertThat(secondEntry.getIndexNames())
                    .contains(ctx.enrichWithSchema("i_clients_last_first"), ctx.enrichWithSchema("i_clients_last_name"));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIntersectedHashIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withDuplicatedHashIndex(), ctx -> {
            final List<DuplicatedIndexes> intersectedIndexes = indexesMaintenance.getIntersectedIndexes(ctx);
            assertThat(intersectedIndexes)
                    .isNotNull()
                    .hasSize(1);
            final DuplicatedIndexes entry = intersectedIndexes.get(0);
            assertThat(entry.getDuplicatedIndexes()).hasSize(2);
            assertThat(entry.getTableName()).isEqualTo(ctx.enrichWithSchema("clients"));
            assertThat(entry.getIndexNames()).contains(ctx.enrichWithSchema("i_clients_last_first"), ctx.enrichWithSchema("i_clients_last_name"));
            assertThat(entry.getTotalSize()).isGreaterThanOrEqualTo(106496L);
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIntersectedIndexesWithDifferentOpclassShouldReturnNothing(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withDifferentOpclassIndexes(), ctx -> {
            final List<DuplicatedIndexes> intersectedIndexes = indexesMaintenance.getIntersectedIndexes(ctx);
            assertThat(intersectedIndexes)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @Test
    void getUnusedIndexesOnEmptyDatabase() {
        final List<UnusedIndex> unusedIndexes = indexesMaintenance.getUnusedIndexes();
        assertThat(unusedIndexes)
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getUnusedIndexesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx -> {
            final List<UnusedIndex> unusedIndexes = indexesMaintenance.getUnusedIndexes(ctx);
            assertThat(unusedIndexes)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getUnusedIndexesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withDuplicatedIndex(), ctx -> {
            final List<UnusedIndex> unusedIndexes = indexesMaintenance.getUnusedIndexes(ctx);
            assertThat(unusedIndexes)
                    .isNotNull()
                    .hasSize(6);
            final Set<String> names = unusedIndexes.stream().map(UnusedIndex::getIndexName).collect(toSet());
            assertThat(names).containsExactlyInAnyOrder(
                    ctx.enrichWithSchema("i_clients_last_first"),
                    ctx.enrichWithSchema("i_clients_last_name"),
                    ctx.enrichWithSchema("i_accounts_account_number"),
                    ctx.enrichWithSchema("i_accounts_number_balance_not_deleted"),
                    ctx.enrichWithSchema("i_accounts_account_number_not_deleted"),
                    ctx.enrichWithSchema("i_accounts_id_account_number_not_deleted")
            );
        });
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnEmptyDatabase() {
        final List<ForeignKey> foreignKeys = indexesMaintenance.getForeignKeysNotCoveredWithIndex();
        assertThat(foreignKeys)
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp, ctx -> {
            final List<ForeignKey> foreignKeys = indexesMaintenance.getForeignKeysNotCoveredWithIndex(ctx);
            assertThat(foreignKeys)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, DatabasePopulator::withReferences, ctx -> {
            final List<ForeignKey> foreignKeys = indexesMaintenance.getForeignKeysNotCoveredWithIndex(ctx);
            assertThat(foreignKeys)
                    .isNotNull()
                    .hasSize(1);
            final ForeignKey foreignKey = foreignKeys.get(0);
            assertThat(foreignKey.getTableName())
                    .isEqualTo(ctx.enrichWithSchema("accounts"));
            assertThat(foreignKey.getConstraintName())
                    .isEqualTo("c_accounts_fk_client_id");
            assertThat(foreignKey.getColumnsInConstraint())
                    .isNotNull()
                    .hasSize(1)
                    .containsExactly(Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id"));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithNotSuitableIndex(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withNonSuitableIndex(), ctx -> {
            final List<ForeignKey> foreignKeys = indexesMaintenance.getForeignKeysNotCoveredWithIndex(ctx);
            assertThat(foreignKeys)
                    .isNotNull()
                    .hasSize(1);
            final ForeignKey foreignKey = foreignKeys.get(0);
            assertThat(foreignKey.getTableName())
                    .isEqualTo(ctx.enrichWithSchema("accounts"));
            assertThat(foreignKey.getConstraintName())
                    .isEqualTo("c_accounts_fk_client_id");
            assertThat(foreignKey.getColumnsInConstraint())
                    .isNotNull()
                    .hasSize(1)
                    .containsExactly(Column.ofNotNull(ctx.enrichWithSchema("accounts"), "client_id"));
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getForeignKeysNotCoveredWithIndexOnDatabaseWithSuitableIndex(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withSuitableIndex(), ctx -> {
            final List<ForeignKey> foreignKeys = indexesMaintenance.getForeignKeysNotCoveredWithIndex(ctx);
            assertThat(foreignKeys)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @Test
    void getIndexesWithNullValuesOnEmptyDatabase() {
        final List<IndexWithNulls> indexes = indexesMaintenance.getIndexesWithNullValues();
        assertThat(indexes)
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIndexesWithNullValuesOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData(), ctx -> {
            final List<IndexWithNulls> indexes = indexesMaintenance.getIndexesWithNullValues(ctx);
            assertThat(indexes)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIndexesWithNullValuesOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withNullValuesInIndex(), ctx -> {
            final List<IndexWithNulls> indexes = indexesMaintenance.getIndexesWithNullValues(ctx);
            assertThat(indexes)
                    .isNotNull()
                    .hasSize(1);
            final IndexWithNulls indexWithNulls = indexes.get(0);
            assertThat(indexWithNulls.getIndexName()).isEqualTo(ctx.enrichWithSchema("i_clients_middle_name"));
            assertThat(indexWithNulls.getNullableColumn())
                    .isEqualTo(Column.ofNotNull(ctx.enrichWithSchema("clients"), "middle_name"));
        });
    }

    @Test
    void getIndexesWithBloatOnEmptyDatabase() {
        final List<IndexWithBloat> indexes = indexesMaintenance.getIndexesWithBloat();
        assertThat(indexes)
                .isNotNull()
                .isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIndexesWithBloatOnDatabaseWithoutThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withStatistics(), ctx -> {
            waitForStatisticsCollector();
            final List<IndexWithBloat> indexes = indexesMaintenance.getIndexesWithBloat(ctx);
            assertThat(indexes)
                    .isNotNull()
                    .isEmpty();
        });
    }

    @ParameterizedTest
    @ValueSource(strings = {"public", "custom"})
    void getIndexesWithBloatOnDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withStatistics(), ctx -> {
            waitForStatisticsCollector();
            assertThat(existsStatisticsForTable(ctx, "accounts")).isTrue();
            final List<IndexWithBloat> indexes = indexesMaintenance.getIndexesWithBloat(ctx);
            assertThat(indexes)
                    .isNotNull()
                    .hasSize(3);
            final IndexWithBloat index = indexes.get(0);
            assertThat(index.getIndexName()).isEqualTo(ctx.enrichWithSchema("accounts_account_number_key"));
            assertThat(index.getTableName()).isEqualTo(ctx.enrichWithSchema("accounts"));
            assertThat(index.getIndexSizeInBytes()).isEqualTo(57344L);
            assertThat(index.getBloatSizeInBytes()).isEqualTo(8192L);
            assertThat(index.getBloatPercentage()).isEqualTo(14);
        });
    }

    @Test
    void securityTest() {
        executeTestOnDatabase("public", dbp -> dbp.withReferences().withData().withNullValuesInIndex(), ctx -> {
            final long before = getRowsCount(ctx.getSchemaName(), "clients");
            assertThat(before).isEqualTo(1001L);
            List<IndexWithNulls> indexes = indexesMaintenance.getIndexesWithNullValues(PgContext.of("; truncate table clients;"));
            assertThat(indexes)
                    .isNotNull()
                    .isEmpty();
            assertThat(getRowsCount(ctx.getSchemaName(), "clients")).isEqualTo(before);
            indexes = indexesMaintenance.getIndexesWithNullValues(PgContext.of("; select pg_sleep(100000000);"));
            assertThat(indexes)
                    .isNotNull()
                    .isEmpty();
            indexes = indexesMaintenance.getIndexesWithNullValues(ctx);
            assertThat(indexes)
                    .isNotNull()
                    .hasSize(1);
        });
    }
}
