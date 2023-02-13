/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.testing;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;
import javax.annotation.Nonnull;

public final class LogsCaptor implements AutoCloseable {

    private final Logger logger;
    private final ListAppender<ILoggingEvent> logAppender;

    public LogsCaptor(@Nonnull final Class<?> type) {
        this(type, Level.INFO);
    }

    public LogsCaptor(@Nonnull final Class<?> type, @Nonnull final Level level) {
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.logger = context.getLogger(type);
        this.logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
        logger.setLevel(level);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        logger.detachAppender(logAppender);
        clear();
    }

    public void clear() {
        logAppender.clearAllFilters();
        logAppender.list.clear();
    }

    @Nonnull
    public List<ILoggingEvent> getLogs() {
        return List.copyOf(logAppender.list);
    }
}
