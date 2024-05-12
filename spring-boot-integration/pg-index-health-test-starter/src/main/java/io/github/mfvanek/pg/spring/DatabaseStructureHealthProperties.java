/*
 * Copyright (c) 2021-2024. Ivan Vakhrushev.
 * https://github.com/mfvanek/pg-index-health-test-starter
 *
 * This file is a part of "pg-index-health-test-starter".
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

/**
 * Represents properties for managing pg-index-health-test-starter configuration.
 *
 * @author Ivan Vakhrushev
 * @since 2021.08.29
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "pg.index.health.test")
public class DatabaseStructureHealthProperties {

    /**
     * Allows to manually disable starter even it presents on classpath.
     */
    private final boolean enabled;

    public DatabaseStructureHealthProperties(@DefaultValue("true") final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public String toString() {
        return DatabaseStructureHealthProperties.class.getSimpleName() + '{' +
            "enabled=" + enabled +
            '}';
    }
}
