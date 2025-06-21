/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.generator;

import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Immutable options to generate sql queries for database migration.
 *
 * @author Ivan Vakhrushev
 * @since 0.5.0
 */
public final class GeneratingOptions {

    /**
     * Neediness to build indexes concurrently.
     */
    private boolean concurrently;
    /**
     * Neediness to exclude null values from indexes to be built.
     */
    private boolean excludeNulls;
    /**
     * Neediness to break long generated sql queries into lines.
     */
    private boolean breakLines;
    /**
     * Indentation size for new lines.
     */
    private int indentation;
    /**
     * Neediness to use capital letters for SQL operators and keywords.
     */
    private boolean uppercaseForKeywords;
    /**
     * Neediness to add "without_nulls" part to the generated index name.
     */
    private boolean nameWithoutNulls;
    /**
     * Position of "idx" in the generated index name.
     */
    private IdxPosition idxPosition;

    private GeneratingOptions(final boolean concurrently,
                              final boolean excludeNulls,
                              final boolean breakLines,
                              final int indentation,
                              final boolean uppercaseForKeywords,
                              final boolean nameWithoutNulls,
                              final IdxPosition idxPosition) {
        this.concurrently = concurrently;
        this.excludeNulls = excludeNulls;
        this.breakLines = breakLines;
        this.indentation = indentation;
        this.uppercaseForKeywords = uppercaseForKeywords;
        this.nameWithoutNulls = nameWithoutNulls;
        this.idxPosition = idxPosition;
    }

    public boolean isConcurrently() {
        return concurrently;
    }

    public boolean isExcludeNulls() {
        return excludeNulls;
    }

    public boolean isBreakLines() {
        return breakLines;
    }

    public int getIndentation() {
        return indentation;
    }

    public boolean isUppercaseForKeywords() {
        return uppercaseForKeywords;
    }

    public boolean isNameWithoutNulls() {
        return nameWithoutNulls;
    }

    public IdxPosition getIdxPosition() {
        return idxPosition;
    }

    public boolean isNeedToAddIdx() {
        return idxPosition != IdxPosition.NONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return GeneratingOptions.class.getSimpleName() + '{' +
            "concurrently=" + concurrently +
            ", excludeNulls=" + excludeNulls +
            ", breakLines=" + breakLines +
            ", indentation=" + indentation +
            ", uppercaseForKeywords=" + uppercaseForKeywords +
            ", nameWithoutNulls=" + nameWithoutNulls +
            ", idxPosition=" + idxPosition +
            '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private @Nullable GeneratingOptions template = new GeneratingOptions(true, true, true, 4, false, true, IdxPosition.SUFFIX);

        private Builder() {
        }

        public GeneratingOptions build() {
            final GeneratingOptions generatingOptions = template();
            template = null;
            return generatingOptions;
        }

        private GeneratingOptions template() {
            if (this.template == null) {
                throw new IllegalStateException("GeneratingOptions object has already been built");
            }
            return this.template;
        }

        /**
         * Use concurrent index building without table locking.
         *
         * @return builder object
         */
        public Builder concurrently() {
            template().concurrently = true;
            return this;
        }

        /**
         * Use regular index building with table locking.
         *
         * @return builder object
         */
        public Builder normally() {
            template().concurrently = false;
            return this;
        }

        public Builder excludeNulls() {
            template().excludeNulls = true;
            return this;
        }

        public Builder includeNulls() {
            template().excludeNulls = false;
            return this;
        }

        public Builder breakLines() {
            template().breakLines = true;
            return this;
        }

        public Builder doNotBreakLines() {
            template().breakLines = false;
            return this;
        }

        public Builder withIndentation(final int indentation) {
            template().indentation = validateIndentation(indentation);
            return this;
        }

        public Builder uppercaseForKeywords() {
            template().uppercaseForKeywords = true;
            return this;
        }

        public Builder lowercaseForKeywords() {
            template().uppercaseForKeywords = false;
            return this;
        }

        public Builder nameWithoutNulls() {
            template().nameWithoutNulls = true;
            return this;
        }

        public Builder doNotNameWithoutNulls() {
            template().nameWithoutNulls = false;
            return this;
        }

        public Builder withIdxPosition(final IdxPosition idxPosition) {
            template().idxPosition = Objects.requireNonNull(idxPosition, "idxPosition cannot be null");
            return this;
        }

        private static int validateIndentation(final int indentation) {
            if (indentation < 0 || indentation > 8) {
                throw new IllegalArgumentException("indentation should be in the range [0, 8]");
            }
            return indentation;
        }
    }
}
