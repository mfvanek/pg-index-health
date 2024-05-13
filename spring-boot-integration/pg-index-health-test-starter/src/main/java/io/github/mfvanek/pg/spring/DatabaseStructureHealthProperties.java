/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
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

    /**
     * Constructs a {@code DatabaseStructureHealthProperties} instance.
     *
     * @param enabled enabled or disabled autoconfiguration
     */
    public DatabaseStructureHealthProperties(@DefaultValue("true") final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the state of autoconfiguration: enabled or disabled.
     *
     * @return true if starter enabled otherwise false
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return DatabaseStructureHealthProperties.class.getSimpleName() + '{' +
            "enabled=" + enabled +
            '}';
    }
}
