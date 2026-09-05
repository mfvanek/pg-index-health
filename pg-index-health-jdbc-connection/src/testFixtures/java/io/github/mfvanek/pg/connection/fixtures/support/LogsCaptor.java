/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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

/**
 * Utility class for capturing log records emitted by a specific logger during its lifetime.
 * <p>
 * This class serves as an in-memory log capturing mechanism, allowing logs to be intercepted
 * and retrieved without relying on log files or external log systems. The captured logs
 * are stored in a thread-safe list and can be later accessed for inspection or testing purposes.
 * <p>
 * Responsibilities:
 * <ul>
 * <li>Temporarily attaches a custom handler to a logger of the specified class.</li>
 * <li>Configures the log level for capturing relevant log messages.</li>
 * <li>Safely detaches the handler and clears all captured logs upon closure.</li>
 * </ul>
 * <p>
 * Features:
 * <ul>
 * <li>Capturing log messages from a specific logger.</li>
 * <li>Retrieving all captured log records after execution.</li>
 * <li>Automatic resource cleanup via the {@link AutoCloseable} interface.</li>
 * </ul>
 * <p>
 * Thread Safety:
 * <ul>
 * <li>Captured log records are stored in a synchronized list, ensuring thread-safe access
 * to the records during tests or concurrent usage.</li>
 * </ul>
 * <p>
 * How to Use:
 * <ul>
 * <li>Instantiate the class with the target logger's class and the desired log level.</li>
 * <li>Execute code that generates logs.</li>
 * <li>Retrieve captured logs via the {@code getLogs()} method.</li>
 * <li>Ensure proper cleanup by closing the {@code LogsCaptor} instance after usage.</li>
 * </ul>
 */
public final class LogsCaptor implements AutoCloseable {

    private final Logger logger;
    private final CapturingHandler handler;

    /**
     * Constructs a {@code LogsCaptor} instance to capture log messages
     * emitted by a logger associated with the specified class.
     *
     * @param type the {@link Class} whose logger should be intercepted; must not be null.
     */
    public LogsCaptor(final Class<?> type) {
        this(type, Level.INFO);
    }

    /**
     * Constructs a {@code LogsCaptor} instance to capture log messages
     * emitted by a logger associated with the specified class, with the
     * specified log level.
     *
     * @param type  the {@link Class} whose logger should be intercepted; must not be null.
     * @param level the {@link Level} that specifies the minimum log level to capture; must not be null.
     */
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
        handler.close();
    }

    /**
     * Retrieves the list of log records captured by the logger associated with this instance.
     *
     * @return an immutable {@link List} of {@link LogRecord} objects representing the log messages that have been intercepted; never null.
     */
    public List<LogRecord> getLogs() {
        return handler.getLogRecords();
    }

    private static class CapturingHandler extends Handler {

        private final List<LogRecord> records = Collections.synchronizedList(new ArrayList<>());

        /**
         * {@inheritDoc}
         */
        @Override
        public void publish(final LogRecord record) {
            if (isLoggable(record)) {
                records.add(record);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void flush() {
            // No-op
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() {
            records.clear();
        }

        private List<LogRecord> getLogRecords() {
            return List.copyOf(records);
        }
    }
}
