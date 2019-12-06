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

    private final Set<String> duplicatedIndexesExclusions;
    private final Set<String> intersectedIndexesExclusions;
    private final Set<String> unusedIndexesExclusions;
    private final Set<String> tablesWithMissingIndexesExclusions;
    private final Set<String> tablesWithoutPrimaryKeyExclusions;
    private final Set<String> indexesWithNullValuesExclusions;

    private Exclusions(@Nonnull String duplicatedIndexesExclusions,
                       @Nonnull String intersectedIndexesExclusions,
                       @Nonnull String unusedIndexesExclusions,
                       @Nonnull String tablesWithMissingIndexesExclusions,
                       @Nonnull String tablesWithoutPrimaryKeyExclusions,
                       @Nonnull String indexesWithNullValuesExclusions) {
        this.duplicatedIndexesExclusions = prepareExclusions(duplicatedIndexesExclusions);
        this.intersectedIndexesExclusions = prepareExclusions(intersectedIndexesExclusions);
        this.unusedIndexesExclusions = prepareExclusions(unusedIndexesExclusions);
        this.tablesWithMissingIndexesExclusions = prepareExclusions(tablesWithMissingIndexesExclusions);
        this.tablesWithoutPrimaryKeyExclusions = prepareExclusions(tablesWithoutPrimaryKeyExclusions);
        this.indexesWithNullValuesExclusions = prepareExclusions(indexesWithNullValuesExclusions);
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
    public Set<String> getDuplicatedIndexesExclusions() {
        return duplicatedIndexesExclusions;
    }

    @Nonnull
    public Set<String> getIntersectedIndexesExclusions() {
        return intersectedIndexesExclusions;
    }

    @Nonnull
    public Set<String> getUnusedIndexesExclusions() {
        return unusedIndexesExclusions;
    }

    @Nonnull
    public Set<String> getTablesWithMissingIndexesExclusions() {
        return tablesWithMissingIndexesExclusions;
    }

    @Nonnull
    public Set<String> getTablesWithoutPrimaryKeyExclusions() {
        return tablesWithoutPrimaryKeyExclusions;
    }

    @Nonnull
    public Set<String> getIndexesWithNullValuesExclusions() {
        return indexesWithNullValuesExclusions;
    }

    @Override
    public String toString() {
        return Exclusions.class.getSimpleName() + '{' +
                "duplicatedIndexesExclusions=" + duplicatedIndexesExclusions +
                ", intersectedIndexesExclusions=" + intersectedIndexesExclusions +
                ", unusedIndexesExclusions=" + unusedIndexesExclusions +
                ", tablesWithMissingIndexesExclusions=" + tablesWithMissingIndexesExclusions +
                ", tablesWithoutPrimaryKeyExclusions=" + tablesWithoutPrimaryKeyExclusions +
                ", indexesWithNullValuesExclusions=" + indexesWithNullValuesExclusions +
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

        private String duplicatedIndexesExclusions = EMPTY;
        private String intersectedIndexesExclusions = EMPTY;
        private String unusedIndexesExclusions = EMPTY;
        private String tablesWithMissingIndexesExclusions = EMPTY;
        private String tablesWithoutPrimaryKeyExclusions = EMPTY;
        private String indexesWithNullValuesExclusions = EMPTY;

        private Builder() {
        }

        public Builder withDuplicatedIndexesExclusions(@Nonnull final String duplicatedIndexesExclusions) {
            this.duplicatedIndexesExclusions = Objects.requireNonNull(duplicatedIndexesExclusions);
            return this;
        }

        public Builder withIntersectedIndexesExclusions(@Nonnull final String intersectedIndexesExclusions) {
            this.intersectedIndexesExclusions = Objects.requireNonNull(intersectedIndexesExclusions);
            return this;
        }

        public Builder withUnusedIndexesExclusions(@Nonnull final String unusedIndexesExclusions) {
            this.unusedIndexesExclusions = Objects.requireNonNull(unusedIndexesExclusions);
            return this;
        }

        public Builder withTablesWithMissingIndexesExclusions(@Nonnull final String tablesWithMissingIndexesExclusions) {
            this.tablesWithMissingIndexesExclusions = Objects.requireNonNull(tablesWithMissingIndexesExclusions);
            return this;
        }

        public Builder withTablesWithoutPrimaryKeyExclusions(@Nonnull final String tablesWithoutPrimaryKeyExclusions) {
            this.tablesWithoutPrimaryKeyExclusions = Objects.requireNonNull(tablesWithoutPrimaryKeyExclusions);
            return this;
        }

        public Builder withIndexesWithNullValuesExclusions(@Nonnull final String indexesWithNullValuesExclusions) {
            this.indexesWithNullValuesExclusions = Objects.requireNonNull(indexesWithNullValuesExclusions);
            return this;
        }

        public Exclusions build() {
            return new Exclusions(
                    duplicatedIndexesExclusions,
                    intersectedIndexesExclusions,
                    unusedIndexesExclusions,
                    tablesWithMissingIndexesExclusions,
                    tablesWithoutPrimaryKeyExclusions,
                    indexesWithNullValuesExclusions);
        }

        @Override
        public String toString() {
            return Builder.class.getSimpleName() + '{' +
                    "duplicatedIndexesExclusions='" + duplicatedIndexesExclusions + '\'' +
                    ", intersectedIndexesExclusions='" + intersectedIndexesExclusions + '\'' +
                    ", unusedIndexesExclusions='" + unusedIndexesExclusions + '\'' +
                    ", tablesWithMissingIndexesExclusions='" + tablesWithMissingIndexesExclusions + '\'' +
                    ", tablesWithoutPrimaryKeyExclusions='" + tablesWithoutPrimaryKeyExclusions + '\'' +
                    ", indexesWithNullValuesExclusions='" + indexesWithNullValuesExclusions + '\'' +
                    '}';
        }
    }
}
