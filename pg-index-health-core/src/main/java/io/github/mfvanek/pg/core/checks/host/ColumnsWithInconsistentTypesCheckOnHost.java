/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.checks.host;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.core.checks.extractors.ColumnWithTypeExtractor;
import io.github.mfvanek.pg.model.column.ColumnWithType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Check for columns that share the same name but have different data types across tables on a specific host.
 * Inconsistent types for the same column name make joins and application code error-prone.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://www.schemacrawler.com/lint.html">SchemaCrawler LinterColumnTypes</a>
 * @since 0.41.1
 */
public class ColumnsWithInconsistentTypesCheckOnHost extends AbstractCheckOnHost<ColumnWithType> {

    /**
     * Constructs a new instance of {@code ColumnsWithInconsistentTypesCheckOnHost}.
     *
     * @param pgConnection the connection to the PostgreSQL database; must not be null
     */
    public ColumnsWithInconsistentTypesCheckOnHost(final PgConnection pgConnection) {
        super(ColumnWithType.class, pgConnection, Diagnostic.COLUMNS_WITH_INCONSISTENT_TYPES, ColumnWithTypeExtractor.of());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Inconsistency is a property of a group of equally named columns rather than of a single column.
     * After the caller's exclusions are applied, a column name may be left with a single distinct type
     * (for example, when service tables such as Liquibase's are filtered out); such residual columns are
     * no longer inconsistent and are dropped from the result.
     */
    @Override
    protected List<ColumnWithType> postProcessResults(final List<ColumnWithType> afterExclusions) {
        return removeConsistentColumns(afterExclusions);
    }

    /**
     * Keeps only the columns whose name is still associated with more than one distinct type.
     * <p>
     * Inconsistency is a property of a group of equally named columns rather than of a single column; once some
     * columns are excluded, a name may be left with a single distinct type and is therefore no longer inconsistent.
     *
     * @param columns the columns remaining after exclusions; must not be null
     * @return the columns that remain genuinely inconsistent, preserving the original order
     */
    static List<ColumnWithType> removeConsistentColumns(final Collection<ColumnWithType> columns) {
        final Map<String, Set<String>> typesByColumnName = columns.stream()
            .collect(Collectors.groupingBy(ColumnWithType::getColumnName,
                Collectors.mapping(ColumnWithType::getColumnType, Collectors.toSet())));
        return columns.stream()
            .filter(column -> typesByColumnName.getOrDefault(column.getColumnName(), Set.of()).size() > 1)
            .toList();
    }
}
