/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package io.github.mfvanek.pg.settings;

import javax.annotation.Nonnull;
import java.util.Set;

public interface ConfigurationMaintenance {

    @Nonnull
    Set<PgParam> getParamsWithDefaultValues(@Nonnull ServerSpecification specification);

    @Nonnull
    Set<PgParam> getParamsCurrentValues();

    @Nonnull
    PgParam getParamCurrentValue(@Nonnull ParamNameAware paramName);
}
