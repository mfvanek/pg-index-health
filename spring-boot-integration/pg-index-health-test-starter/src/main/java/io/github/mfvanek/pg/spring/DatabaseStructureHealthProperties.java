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

import io.github.mfvanek.pg.model.validation.Validators;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.annotation.concurrent.Immutable;

/**
 * Represents properties for managing pg-index-health-test-starter configuration.
 *
 * @author Ivan Vakhrushev
 * @since 2021.08.29
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "pg.index.health.test")
@Immutable
public class DatabaseStructureHealthProperties {

    /**
     * Indicates whether the starter is enabled, even if it is present on the classpath.
     * This allows for manual control over autoconfiguration.
     * <p>
     * Default value: {@code true}.
     * </p>
     */
    private final boolean enabled;

    /**
     * The name of the datasource bean to use for health checks.
     * <p>
     * Default value: {@code "dataSource"}.
     * </p>
     */
    private final String datasourceBeanName;

    /**
     * The name of the datasource URL property used in the configuration.
     * <p>
     * Default value: {@code "spring.datasource.url"}.
     * </p>
     */
    private final String datasourceUrlPropertyName;

    /**
     * Constructs a new {@code DatabaseStructureHealthProperties} instance with the specified values.
     *
     * @param enabled                   whether the autoconfiguration is enabled (default: {@code true})
     * @param datasourceBeanName        the name of the datasource bean (default: {@code "dataSource"}, must not be blank)
     * @param datasourceUrlPropertyName the name of the datasource URL property (default: {@code "spring.datasource.url"}, must not be blank)
     * @throws IllegalArgumentException if {@code datasourceBeanName} or {@code datasourceUrlPropertyName} is blank
     */
    public DatabaseStructureHealthProperties(@DefaultValue("true") final boolean enabled,
                                             @DefaultValue("dataSource") final String datasourceBeanName,
                                             @DefaultValue("spring.datasource.url") final String datasourceUrlPropertyName) {
        this.enabled = enabled;
        this.datasourceBeanName = Validators.notBlank(datasourceBeanName, "datasourceBeanName");
        this.datasourceUrlPropertyName = Validators.notBlank(datasourceUrlPropertyName, "datasourceUrlPropertyName");
    }

    /**
     * Checks if the autoconfiguration is enabled.
     *
     * @return {@code true} if the starter is enabled; {@code false} otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Retrieves the name of the datasource bean to be used.
     *
     * @return the name of the datasource bean (default: {@code "dataSource"})
     */
    public String getDatasourceBeanName() {
        return datasourceBeanName;
    }

    /**
     * Retrieves the name of the datasource URL property.
     *
     * @return the name of the datasource URL property (default: {@code "spring.datasource.url"})
     */
    public String getDatasourceUrlPropertyName() {
        return datasourceUrlPropertyName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return DatabaseStructureHealthProperties.class.getSimpleName() + '{' +
            "enabled=" + enabled +
            ", datasourceBeanName='" + datasourceBeanName + '\'' +
            ", datasourceUrlPropertyName='" + datasourceUrlPropertyName + '\'' +
            '}';
    }
}
