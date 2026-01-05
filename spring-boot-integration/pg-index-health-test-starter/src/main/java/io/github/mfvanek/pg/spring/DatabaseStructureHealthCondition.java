/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring;

import io.github.mfvanek.pg.connection.host.PgUrlParser;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Custom {@link SpringBootCondition} to disable starter when configured a data source to another database (not PostgreSQL).
 */
public class DatabaseStructureHealthCondition extends SpringBootCondition {

    /**
     * {@inheritDoc}
     */
    @Override
    public ConditionOutcome getMatchOutcome(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        final ConditionMessage.Builder message = ConditionMessage.forCondition("pg.index.health.test PostgreSQL condition");
        final String datasourceUrlPropertyName = getDatasourceUrlPropertyName(context);
        final String jdbcUrl = getJdbcUrl(context, datasourceUrlPropertyName);
        if (jdbcUrl != null && !jdbcUrl.isBlank()) {
            if (jdbcUrl.startsWith(PgUrlParser.URL_HEADER) || jdbcUrl.startsWith(PgUrlParser.TESTCONTAINERS_PG_URL_PREFIX)) {
                return ConditionOutcome.match(message.foundExactly("found PostgreSQL connection " + jdbcUrl));
            }
            return ConditionOutcome.noMatch(message.notAvailable("not PostgreSQL connection"));
        }
        return ConditionOutcome.match(message.didNotFind(datasourceUrlPropertyName).items());
    }

    @Nullable
    private static String getJdbcUrl(final ConditionContext context,
                                     final String datasourceUrlPropertyName) {
        return Binder.get(context.getEnvironment())
            .bind(datasourceUrlPropertyName, String.class)
            .orElse(null);
    }

    private static String getDatasourceUrlPropertyName(final ConditionContext context) {
        return Binder.get(context.getEnvironment())
            .bind("pg.index.health.test.datasource-url-property-name", String.class)
            .orElse(DatabaseStructureHealthProperties.STANDARD_DATASOURCE_URL_PROPERTY_NAME);
    }
}
