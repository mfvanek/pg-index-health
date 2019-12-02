/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health.logger;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Exclusions {

    private final Set<String> duplicatedIndicesExclusions;
    private final Set<String> intersectedIndicesExclusions;
    private final Set<String> unusedIndicesExclusions;
    private final Set<String> tablesWithMissingIndicesExclusions;
    private final Set<String> tablesWithoutPrimaryKeyExclusions;
    private final Set<String> indicesWithNullValuesExclusions;

    private Exclusions(@Nonnull String duplicatedIndicesExclusions,
                       @Nonnull String intersectedIndicesExclusions,
                       @Nonnull String unusedIndicesExclusions,
                       @Nonnull String tablesWithMissingIndicesExclusions,
                       @Nonnull String tablesWithoutPrimaryKeyExclusions,
                       @Nonnull String indicesWithNullValuesExclusions) {
        this.duplicatedIndicesExclusions = prepareExclusions(duplicatedIndicesExclusions);
        this.intersectedIndicesExclusions = prepareExclusions(intersectedIndicesExclusions);
        this.unusedIndicesExclusions = prepareExclusions(unusedIndicesExclusions);
        this.tablesWithMissingIndicesExclusions = prepareExclusions(tablesWithMissingIndicesExclusions);
        this.tablesWithoutPrimaryKeyExclusions = prepareExclusions(tablesWithoutPrimaryKeyExclusions);
        this.indicesWithNullValuesExclusions = prepareExclusions(indicesWithNullValuesExclusions);
    }

    private static Set<String> prepareExclusions(@Nonnull final String rawExclusions) {
        Objects.requireNonNull(rawExclusions);
        final Set<String> exclusions = new HashSet<>();
        if (StringUtils.isNotBlank(rawExclusions)) {
            final String[] tables = rawExclusions.toLowerCase().split(",");
            for (String tableName : tables) {
                if (StringUtils.isNotBlank(tableName)) {
                    exclusions.add(tableName.trim());
                }
            }
        }
        return exclusions;
    }

    @Nonnull
    public Set<String> getDuplicatedIndicesExclusions() {
        return duplicatedIndicesExclusions;
    }

    @Nonnull
    public Set<String> getIntersectedIndicesExclusions() {
        return intersectedIndicesExclusions;
    }

    @Nonnull
    public Set<String> getUnusedIndicesExclusions() {
        return unusedIndicesExclusions;
    }

    @Nonnull
    public Set<String> getTablesWithMissingIndicesExclusions() {
        return tablesWithMissingIndicesExclusions;
    }

    @Nonnull
    public Set<String> getTablesWithoutPrimaryKeyExclusions() {
        return tablesWithoutPrimaryKeyExclusions;
    }

    @Nonnull
    public Set<String> getIndicesWithNullValuesExclusions() {
        return indicesWithNullValuesExclusions;
    }

    @Override
    public String toString() {
        return Exclusions.class.getSimpleName() + '{' +
                "duplicatedIndicesExclusions=" + duplicatedIndicesExclusions +
                ", intersectedIndicesExclusions=" + intersectedIndicesExclusions +
                ", unusedIndicesExclusions=" + unusedIndicesExclusions +
                ", tablesWithMissingIndicesExclusions=" + tablesWithMissingIndicesExclusions +
                ", tablesWithoutPrimaryKeyExclusions=" + tablesWithoutPrimaryKeyExclusions +
                ", indicesWithNullValuesExclusions=" + indicesWithNullValuesExclusions +
                '}';
    }

    public static Exclusions empty() {
        return builder().build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private static final String EMPTY = "";

        private String duplicatedIndicesExclusions = EMPTY;
        private String intersectedIndicesExclusions = EMPTY;
        private String unusedIndicesExclusions = EMPTY;
        private String tablesWithMissingIndicesExclusions = EMPTY;
        private String tablesWithoutPrimaryKeyExclusions = EMPTY;
        private String indicesWithNullValuesExclusions = EMPTY;

        private Builder() {
        }

        public Builder withDuplicatedIndicesExclusions(@Nonnull final String duplicatedIndicesExclusions) {
            this.duplicatedIndicesExclusions = Objects.requireNonNull(duplicatedIndicesExclusions);
            return this;
        }

        public Builder withIntersectedIndicesExclusions(@Nonnull final String intersectedIndicesExclusions) {
            this.intersectedIndicesExclusions = Objects.requireNonNull(intersectedIndicesExclusions);
            return this;
        }

        public Builder withUnusedIndicesExclusions(@Nonnull final String unusedIndicesExclusions) {
            this.unusedIndicesExclusions = Objects.requireNonNull(unusedIndicesExclusions);
            return this;
        }

        public Builder withTablesWithMissingIndicesExclusions(@Nonnull final String tablesWithMissingIndicesExclusions) {
            this.tablesWithMissingIndicesExclusions = Objects.requireNonNull(tablesWithMissingIndicesExclusions);
            return this;
        }

        public Builder withTablesWithoutPrimaryKeyExclusions(@Nonnull final String tablesWithoutPrimaryKeyExclusions) {
            this.tablesWithoutPrimaryKeyExclusions = Objects.requireNonNull(tablesWithoutPrimaryKeyExclusions);
            return this;
        }

        public Builder withIndicesWithNullValuesExclusions(@Nonnull final String indicesWithNullValuesExclusions) {
            this.indicesWithNullValuesExclusions = Objects.requireNonNull(indicesWithNullValuesExclusions);
            return this;
        }

        public Exclusions build() {
            return new Exclusions(
                    duplicatedIndicesExclusions,
                    intersectedIndicesExclusions,
                    unusedIndicesExclusions,
                    tablesWithMissingIndicesExclusions,
                    tablesWithoutPrimaryKeyExclusions,
                    indicesWithNullValuesExclusions);
        }

        @Override
        public String toString() {
            return Builder.class.getSimpleName() + '{' +
                    "duplicatedIndicesExclusions='" + duplicatedIndicesExclusions + '\'' +
                    ", intersectedIndicesExclusions='" + intersectedIndicesExclusions + '\'' +
                    ", unusedIndicesExclusions='" + unusedIndicesExclusions + '\'' +
                    ", tablesWithMissingIndicesExclusions='" + tablesWithMissingIndicesExclusions + '\'' +
                    ", tablesWithoutPrimaryKeyExclusions='" + tablesWithoutPrimaryKeyExclusions + '\'' +
                    ", indicesWithNullValuesExclusions='" + indicesWithNullValuesExclusions + '\'' +
                    '}';
        }
    }
}
