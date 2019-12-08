/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.settings;

import javax.annotation.Nonnull;
import java.util.List;

public interface ConfigurationMaintenance {

    @Nonnull
    List<PgParam> getParamsWithDefaultValues(@Nonnull ServerSpecification specification);
}
