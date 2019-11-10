package com.mfvanek.pg.index.health;

import java.util.List;
import java.util.Map;

public interface ConfigurationMaintenance {

    List<Map.Entry<String, String>> getParamsWithDefaultValues();
}
