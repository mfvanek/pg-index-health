/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import io.github.mfvanek.pg.common.maintenance.DatabaseChecks;
import io.github.mfvanek.pg.connection.ConnectionCredentials;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnectionFactory;
import io.github.mfvanek.pg.utils.ClockHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;
import javax.annotation.Nonnull;

/**
 * Outputs summary about database health to a file with fixed name 'key-value.log'.
 * <p>
 * Example of output:
 * <pre><code>
 * 2021-11-03T08:59:39.000439Z  db_indexes_health  invalid_indexes  1
 * 2021-11-03T08:59:39.010893Z  db_indexes_health  duplicated_indexes  1
 * 2021-11-03T08:59:39.014995Z  db_indexes_health  intersected_indexes  2
 * 2021-11-03T08:59:39.035153Z  db_indexes_health  unused_indexes  0
 * 2021-11-03T08:59:39.038642Z  db_indexes_health  foreign_keys_without_index  3
 * 2021-11-03T08:59:39.042304Z  db_indexes_health  tables_with_missing_indexes  0
 * 2021-11-03T08:59:39.044463Z  db_indexes_health  tables_without_primary_key  1
 * 2021-11-03T08:59:39.047406Z  db_indexes_health  indexes_with_null_values  1
 * 2021-11-03T08:59:39.059082Z  db_indexes_health  indexes_bloat  0
 * 2021-11-03T08:59:39.066066Z  db_indexes_health  tables_bloat  0
 * </code></pre>
 *
 * @author Ivan Vakhrushev
 */
public class KeyValueFileHealthLogger extends AbstractHealthLogger {

    private static final Logger KV_LOG = LoggerFactory.getLogger("key-value.log");

    public KeyValueFileHealthLogger(@Nonnull final ConnectionCredentials credentials,
                                    @Nonnull final HighAvailabilityPgConnectionFactory connectionFactory,
                                    @Nonnull final Function<HighAvailabilityPgConnection, DatabaseChecks> databaseChecksFactory) {
        super(credentials, connectionFactory, databaseChecksFactory);
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
                ZonedDateTime.now(ClockHolder.clock())) + "\t" + keyName + "\t" + subKeyName + "\t" + value;
    }
}
