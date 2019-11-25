/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

public interface IndexMaintenanceFactory {

    IndexMaintenance forDataSource(@Nonnull DataSource dataSource);
}
