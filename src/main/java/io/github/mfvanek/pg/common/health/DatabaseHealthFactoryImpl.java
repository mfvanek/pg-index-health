/*
 * Copyright (c) 2019-2021. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health;

import io.github.mfvanek.pg.common.maintenance.MaintenanceFactory;
import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;

import javax.annotation.Nonnull;
import java.util.Objects;

public class DatabaseHealthFactoryImpl implements DatabaseHealthFactory {

    private final MaintenanceFactory maintenanceFactory;

    public DatabaseHealthFactoryImpl(@Nonnull final MaintenanceFactory maintenanceFactory) {
        this.maintenanceFactory = Objects.requireNonNull(maintenanceFactory);
    }

    @Nonnull
    @Override
    public DatabaseHealth of(@Nonnull HighAvailabilityPgConnection haPgConnection) {
        return new DatabaseHealthImpl(haPgConnection, maintenanceFactory);
    }
}
