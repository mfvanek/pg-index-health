/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.connection;

import javax.annotation.Nonnull;
import javax.sql.DataSource;

/**
 * A wrapper of standard DataSource interface with awareness of real host.
 */
public interface PgConnection extends HostAware {

    @Nonnull
    DataSource getDataSource();
}
