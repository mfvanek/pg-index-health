/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.cluster;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.mfvanek.pg.checks.predicates.FilterIndexesByNamePredicate;
import io.github.mfvanek.pg.common.maintenance.DatabaseCheckOnCluster;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.index.IndexNameAware;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.statistics.maintenance.StatisticsMaintenanceOnHost;
import io.github.mfvanek.pg.support.DatabaseAwareTestBase;
import io.github.mfvanek.pg.utils.ClockHolder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static io.github.mfvanek.pg.checks.cluster.UnusedIndexesCheckOnCluster.getLastStatsResetDateLogMessage;
import static io.github.mfvanek.pg.checks.cluster.UnusedIndexesCheckOnCluster.getResultAsIntersection;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("PMD.ExcessiveImports")
class UnusedIndexesCheckOnClusterTest extends DatabaseAwareTestBase {

    private static Logger logger;
    private static ListAppender<ILoggingEvent> logAppender;

    private final DatabaseCheckOnCluster<UnusedIndex> check = new UnusedIndexesCheckOnCluster(getHaPgConnection());

    @BeforeAll
    static void init() {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        logger = context.getLogger(UnusedIndexesCheckOnCluster.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @BeforeEach
    void setUp() {
        logger.setLevel(Level.INFO);
        logAppender.clearAllFilters();
        logAppender.list.clear();
    }

    @Test
    void shouldSatisfyContract() {
        assertThat(check.getType()).isEqualTo(UnusedIndex.class);
        assertThat(check.getDiagnostic()).isEqualTo(Diagnostic.UNUSED_INDEXES);
    }

    @Test
    void checkOnClusterShouldLogResetStatisticsData() {
        assertThat(check.check())
                .isEmpty();

        assertThat(logAppender.list)
                .hasSize(1)
                .allMatch(l -> l.getMessage().contains("reset"));
    }

    @ParameterizedTest
    @ValueSource(strings = {PgContext.DEFAULT_SCHEMA_NAME, "custom"})
    void onDatabaseWithThem(final String schemaName) {
        executeTestOnDatabase(schemaName, dbp -> dbp.withReferences().withData().withDuplicatedIndex(), ctx -> {
            assertThat(check.check(ctx))
                    .hasSize(6)
                    .containsExactlyInAnyOrder(
                            UnusedIndex.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_last_first"), 0L, 0),
                            UnusedIndex.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_last_name"), 0L, 0),
                            UnusedIndex.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_account_number"), 0L, 0),
                            UnusedIndex.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_number_balance_not_deleted"), 0L, 0),
                            UnusedIndex.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_account_number_not_deleted"), 0L, 0),
                            UnusedIndex.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_id_account_number_not_deleted"), 0L, 0))
                    .allMatch(i -> i.getIndexSizeInBytes() > 0L)
                    .allMatch(i -> i.getIndexScans() == 0);

            final Predicate<IndexNameAware> predicate = FilterIndexesByNamePredicate.of(
                    Arrays.asList(ctx.enrichWithSchema("i_clients_last_first"), ctx.enrichWithSchema("i_accounts_account_number")));
            assertThat(check.check(ctx, predicate))
                    .hasSize(4)
                    .containsExactlyInAnyOrder(
                            UnusedIndex.of(ctx.enrichWithSchema("clients"), ctx.enrichWithSchema("i_clients_last_name"), 0L, 0),
                            UnusedIndex.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_number_balance_not_deleted"), 0L, 0),
                            UnusedIndex.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_account_number_not_deleted"), 0L, 0),
                            UnusedIndex.of(ctx.enrichWithSchema("accounts"), ctx.enrichWithSchema("i_accounts_id_account_number_not_deleted"), 0L, 0))
                    .allMatch(i -> i.getIndexSizeInBytes() > 0L)
                    .allMatch(i -> i.getIndexScans() == 0);
        });
    }

    @Test
    void getResultAsIntersectionShouldWork() {
        final UnusedIndex i1 = UnusedIndex.of("t1", "i1", 1L, 1L);
        final UnusedIndex i2 = UnusedIndex.of("t1", "i2", 2L, 2L);
        final UnusedIndex i3 = UnusedIndex.of("t2", "i3", 3L, 3L);
        final UnusedIndex i4 = UnusedIndex.of("t3", "i4", 4L, 4L);
        final UnusedIndex i5 = UnusedIndex.of("t3", "i5", 5L, 5L);
        final List<List<UnusedIndex>> potentiallyUnusedIndexesFromAllHosts = Arrays.asList(
                Arrays.asList(i5, i4, i1, i3),
                Arrays.asList(i2, i1, i5),
                Arrays.asList(i2, i5, i1, i4));
        assertThat(getResultAsIntersection(potentiallyUnusedIndexesFromAllHosts))
                .hasSize(2)
                .containsExactlyInAnyOrder(i1, i5);
    }

    @Test
    void getResultAsIntersectionWithEmptyInput() {
        assertThat(getResultAsIntersection(Collections.emptyList()))
                .isEmpty();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void getLastStatsResetDateLogMessageWithWrongArguments() {
        assertThatThrownBy(() -> getLastStatsResetDateLogMessage(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("statisticsMaintenance cannot be null");
    }

    @Test
    void getLastStatsResetDateLogMessageWithoutResetTimestamp() {
        final StatisticsMaintenanceOnHost statisticsMaintenance = Mockito.mock(StatisticsMaintenanceOnHost.class);
        Mockito.when(statisticsMaintenance.getLastStatsResetTimestamp()).thenReturn(Optional.empty());
        final String logMessage = getLastStatsResetDateLogMessage(statisticsMaintenance);
        assertThat(logMessage)
                .isEqualTo("Statistics have never been reset on this host");
    }

    @Test
    void getLastStatsResetDateLogMessageWithResetTimestamp() {
        final OffsetDateTime resetDate = OffsetDateTime.now(ClockHolder.clock());
        final StatisticsMaintenanceOnHost statisticsMaintenance = Mockito.mock(StatisticsMaintenanceOnHost.class);
        Mockito.when(statisticsMaintenance.getLastStatsResetTimestamp()).thenReturn(Optional.of(resetDate.minusDays(123L)));
        final String logMessage = getLastStatsResetDateLogMessage(statisticsMaintenance);
        assertThat(logMessage)
                .startsWith("Last statistics reset on this host was 123 days ago (");
    }
}
