/*
 * Copyright (c) 2021-2024. Ivan Vakhrushev.
 * https://github.com/mfvanek/pg-index-health-test-starter
 *
 * This file is a part of "pg-index-health-test-starter".
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import javax.annotation.Nullable;

public class DatabaseStructureHealthCondition extends SpringBootCondition {

    private static final String PROPERTY_NAME = "spring.datasource.url";

    @Override
    public ConditionOutcome getMatchOutcome(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
        final ConditionMessage.Builder message = ConditionMessage.forCondition("pg.index.health.test PostgreSQL condition");
        final String jdbcUrl = getJdbcUrl(context);
        if (jdbcUrl != null && !jdbcUrl.isBlank()) {
            if (jdbcUrl.startsWith("jdbc:postgresql://")) {
                return ConditionOutcome.match(message.foundExactly("found PostgreSQL connection " + jdbcUrl));
            }
            return ConditionOutcome.noMatch(message.notAvailable("not PostgreSQL connection"));
        }
        return ConditionOutcome.match(message.didNotFind(PROPERTY_NAME).items());
    }

    @Nullable
    private static String getJdbcUrl(final ConditionContext context) {
        return Binder.get(context.getEnvironment())
            .bind(PROPERTY_NAME, String.class)
            .orElse(null);
    }
}
