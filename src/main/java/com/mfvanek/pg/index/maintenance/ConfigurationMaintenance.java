/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import java.util.List;
import java.util.Map;

public interface ConfigurationMaintenance {

    List<Map.Entry<String, String>> getParamsWithDefaultValues();
}
