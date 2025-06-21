/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.connection.fixtures.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class LogsCaptor implements AutoCloseable {

    private final Logger logger;
    private final CapturingHandler handler;

    public LogsCaptor(final Class<?> type) {
        this(type, Level.INFO);
    }

    public LogsCaptor(final Class<?> type, final Level level) {
        this.logger = Logger.getLogger(type.getName());
        this.handler = new CapturingHandler();
        this.handler.setLevel(level);

        logger.setUseParentHandlers(false);

        logger.addHandler(handler);
        logger.setLevel(level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        logger.removeHandler(handler);
        clear();
    }

    public void clear() {
        handler.clear();
    }

    public List<LogRecord> getLogs() {
        return handler.getLogRecords();
    }

    private static class CapturingHandler extends Handler {

        private final List<LogRecord> records = Collections.synchronizedList(new ArrayList<>());

        @Override
        public void publish(final LogRecord record) {
            if (isLoggable(record)) {
                records.add(record);
            }
        }

        @Override
        public void flush() {
            // No-op
        }

        @Override
        public void close() {
            records.clear();
        }

        public List<LogRecord> getLogRecords() {
            return List.copyOf(records);
        }

        public void clear() {
            records.clear();
        }
    }
}
