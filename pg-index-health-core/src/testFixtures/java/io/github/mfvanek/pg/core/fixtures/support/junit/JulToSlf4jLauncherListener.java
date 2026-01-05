/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.junit;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

public class JulToSlf4jLauncherListener implements LauncherSessionListener {

    private static final Logger ROOT_LOGGER = Logger.getLogger("io.github.mfvanek.pg");

    /**
     * {@inheritDoc}
     */
    @Override
    public void launcherSessionOpened(final LauncherSession session) {
        // Configure SLF4J Simple
        System.setProperty("org.slf4j.simpleLogger.showDateTime", "true");
        System.setProperty("org.slf4j.simpleLogger.dateTimeFormat", "HH:mm:ss.SSS");
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
        System.setProperty("org.slf4j.simpleLogger.log.io.github.mfvanek.pg", "trace");
        System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");

        // Remove existing handlers and install bridge before anything else
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();

        ROOT_LOGGER.setLevel(Level.ALL);
    }
}
