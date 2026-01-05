/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.extractors;

import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;
import io.github.mfvanek.pg.model.table.Table;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("fast")
class TableExtractorTest {

    @Test
    void shouldCreateInstance() {
        assertThat(TableExtractor.of())
            .isNotNull()
            .isInstanceOf(TableExtractor.class);
    }

    @Test
    void mapRowShouldWork() throws SQLException {
        try (ResultSet rs = Mockito.mock(ResultSet.class)) {
            final ResultSetExtractor<@NonNull Table> extractor = TableExtractor.of();
            Mockito.when(rs.getString(Mockito.anyString())).thenReturn("tst");
            Mockito.when(rs.getLong(Mockito.anyString())).thenReturn(11L);
            assertThat(extractor.mapRow(rs, Integer.MAX_VALUE))
                .usingRecursiveComparison()
                .isEqualTo(Table.of("tst", 11L));
        }
    }
}
