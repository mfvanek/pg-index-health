/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import com.mfvanek.pg.index.health.IndicesHealth;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class AbstractIndicesHealthLogger implements IndicesHealthLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractIndicesHealthLogger.class);

    private final IndicesHealth indicesHealth;
    private final Exclusions exclusions;

    protected AbstractIndicesHealthLogger(@Nonnull final IndicesHealth indicesHealth,
                                          @Nonnull final Exclusions exclusions) {
        this.indicesHealth = Objects.requireNonNull(indicesHealth);
        this.exclusions = Objects.requireNonNull(exclusions);
    }

    @Override
    public final void logAll() {
        logInvalidIndices();
        logDuplicatedIndices();
        logIntersectedIndices();
        logUnusedIndices();
        logForeignKeysNotCoveredWithIndex();
        logTablesWithMissingIndices();
        logTablesWithoutPrimaryKey();
        logIndicesWithNullValues();
    }

    protected abstract void writeToLog(@Nonnull String keyName, @Nonnull String subKeyName, int value);

    private void writeToLog(@Nonnull final String subKeyName, final int value) {
        writeToLog("db_indices_health", subKeyName, value);
    }

    private void writeZeroToLog(@Nonnull final String subKeyName) {
        writeToLog(subKeyName, 0);
    }

    private void logInvalidIndices() {
        final var invalidIndices = indicesHealth.getInvalidIndices();
        final String subKeyName = "invalid_indices";
        if (CollectionUtils.isNotEmpty(invalidIndices)) {
            writeToLog(subKeyName, invalidIndices.size());
            LOGGER.error("There are invalid indices in database {}", invalidIndices);
        } else {
            writeZeroToLog(subKeyName);
        }
    }

    private void logDuplicatedIndices() {
        throw new UnsupportedOperationException();
    }

    private void logIntersectedIndices() {
        throw new UnsupportedOperationException();
    }

    private void logUnusedIndices() {
        throw new UnsupportedOperationException();
    }

    private void logForeignKeysNotCoveredWithIndex() {
        throw new UnsupportedOperationException();
    }

    private void logTablesWithMissingIndices() {
        throw new UnsupportedOperationException();
    }

    private void logTablesWithoutPrimaryKey() {
        throw new UnsupportedOperationException();
    }

    private void logIndicesWithNullValues() {
        throw new UnsupportedOperationException();
    }
}
