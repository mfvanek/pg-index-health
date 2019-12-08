/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.settings;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public interface ConfigurationMaintenance {

    @Nonnull
    List<Map.Entry<String, String>> getParamsWithDefaultValues(@Nonnull ServerSpecification specification);
}
