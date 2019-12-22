/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import com.mfvanek.pg.index.health.IndexesHealth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleHealthLogger extends AbstractIndexesHealthLogger {

    private static final Logger KV_LOG = LoggerFactory.getLogger("key-value.log");

    public SimpleHealthLogger(@Nonnull final IndexesHealth indexesHealth) {
        super(indexesHealth);
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
