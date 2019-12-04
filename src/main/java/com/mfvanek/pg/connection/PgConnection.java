/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

public interface PgConnection extends HostAware {

    @Nonnull
    DataSource getDataSource();
}
