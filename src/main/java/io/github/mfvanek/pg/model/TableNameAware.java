/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.model;

import javax.annotation.Nonnull;

public interface TableNameAware {

    @Nonnull
    String getTableName();
}
