/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import com.mfvanek.pg.connection.PgConnection;

import javax.annotation.Nonnull;

public interface IndexMaintenanceFactory {

    @Nonnull
    IndexMaintenance forConnection(@Nonnull PgConnection pgConnection);
}
