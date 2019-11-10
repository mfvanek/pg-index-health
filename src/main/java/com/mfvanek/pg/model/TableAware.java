package com.mfvanek.pg.model;

import javax.annotation.Nonnull;

public interface TableAware {

    @Nonnull
    String getTableName();
}
