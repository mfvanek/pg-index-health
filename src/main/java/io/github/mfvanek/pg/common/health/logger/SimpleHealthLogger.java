/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.common.health.DatabaseHealth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleHealthLogger extends AbstractHealthLogger {

    private static final Logger KV_LOG = LoggerFactory.getLogger("key-value.log");

    public SimpleHealthLogger(@Nonnull final DatabaseHealth databaseHealth) {
        super(databaseHealth);
    }

    @Override
    protected String writeToLog(@Nonnull final LoggingKey key, final int value) {
        final String result = format(key.getKeyName(), key.getSubKeyName(), value);
        KV_LOG.info(result);
        return result;
    }

    @Nonnull
    private String format(@Nonnull final String keyName, @Nonnull final String subKeyName, final int value) {
        return DateTimeFormatter.ISO_INSTANT.format(
                ZonedDateTime.now()) + "\t" + keyName + "\t" + subKeyName + "\t" + value;
    }
}
