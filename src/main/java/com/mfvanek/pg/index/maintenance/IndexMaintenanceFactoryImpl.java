/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.connection.PgConnection;

import javax.annotation.Nonnull;

public class IndexMaintenanceFactoryImpl implements IndexMaintenanceFactory {

    @Override
    @Nonnull
    public IndexMaintenance forConnection(@Nonnull final PgConnection pgConnection) {
        return new IndexMaintenanceImpl(pgConnection);
    }
}
