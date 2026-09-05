/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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
 * Immutable options to generate SQL queries for database migration.
 *
 * <p>Instances are created using the {@link #builder()} method:
 *
 * <pre>{@code
 * GeneratingOptions options = GeneratingOptions.builder()
 *     .concurrently()
 *     .excludeNulls()
 *     .breakLines()
 *     .withIndentation(4)
 *     .uppercaseForKeywords()
 *     .nameWithoutNulls()
 *     .withIdxPosition(IdxPosition.SUFFIX)
 *     .build();
 * }</pre>
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
     * Neediness to break long generated SQL queries into lines.
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
     * Neediness to add the "without_nulls" part to the generated index name.
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

    /**
     * Returns whether indexes should be built concurrently.
     *
     * @return {@code true} if concurrent index creation is enabled;
     *         {@code false} if regular index creation should be used
     */
    public boolean isConcurrently() {
        return concurrently;
    }

    /**
     * Returns whether {@code NULL} values should be excluded from indexes.
     *
     * @return {@code true} if {@code NULL} values should be excluded;
     *         {@code false} if they should be included
     */
    public boolean isExcludeNulls() {
        return excludeNulls;
    }

    /**
     * Returns whether generated SQL statements should be split across multiple lines.
     *
     * @return {@code true} if long SQL statements should be formatted across multiple lines;
     *         {@code false} otherwise
     */
    public boolean isBreakLines() {
        return breakLines;
    }

    /**
     * Returns the number of spaces used to indent continuation lines in generated SQL.
     *
     * @return indentation size in spaces
     */
    public int getIndentation() {
        return indentation;
    }

    /**
     * Returns whether SQL operators and keywords should be generated using uppercase letters.
     *
     * @return {@code true} for uppercase SQL keywords; {@code false} for lowercase keywords
     */
    public boolean isUppercaseForKeywords() {
        return uppercaseForKeywords;
    }

    /**
     * Returns whether the {@code without_nulls} part should be included in generated index names.
     *
     * @return {@code true} if the {@code without_nulls} suffix should be included;
     *         {@code false} otherwise
     */
    public boolean isNameWithoutNulls() {
        return nameWithoutNulls;
    }

    /**
     * Returns the position of the {@code idx} part in generated index names.
     *
     * @return configured {@link IdxPosition}
     */
    public IdxPosition getIdxPosition() {
        return idxPosition;
    }

    /**
     * Determines whether the generated index name should contain an {@code idx} marker.
     *
     * @return {@code true} if {@link #getIdxPosition()} is not {@link IdxPosition#NONE};
     *         {@code false} otherwise
     */
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

    /**
     * Returns a builder for creating {@code GeneratingOptions}.
     *
     * @return a new builder initialized with the default options
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link GeneratingOptions}.
     *
     * <p>A builder can be used to customize SQL generation options before creating
     * a {@code GeneratingOptions} instance.
     */
    public static final class Builder {

        private @Nullable GeneratingOptions template = new GeneratingOptions(true, true, true, 4, false, true, IdxPosition.SUFFIX);

        private Builder() {
        }

        /**
         * Builds the configured generation options.
         *
         * <p>The builder can only be used once. Calling this method more than once
         * results in an {@link IllegalStateException}.
         *
         * @return configured generation options
         * @throws IllegalStateException if this builder has already built an instance
         */
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

        /**
         * Configures generated indexes to exclude rows with {@code NULL} values.
         *
         * @return this builder
         */
        public Builder excludeNulls() {
            template().excludeNulls = true;
            return this;
        }

        /**
         * Configures generated indexes to include rows with {@code NULL} values.
         *
         * @return this builder
         */
        public Builder includeNulls() {
            template().excludeNulls = false;
            return this;
        }

        /**
         * Enables multi-line formatting for generated SQL statements.
         *
         * @return this builder
         */
        public Builder breakLines() {
            template().breakLines = true;
            return this;
        }

        /**
         * Disables multi-line formatting for generated SQL statements.
         *
         * @return this builder
         */
        public Builder doNotBreakLines() {
            template().breakLines = false;
            return this;
        }

        /**
         * Sets the indentation used for continuation lines in generated SQL.
         *
         * <p>The indentation must be between {@code 0} and {@code 8} spaces,
         * inclusive.
         *
         * @param indentation indentation size in spaces
         * @return this builder
         * @throws IllegalArgumentException if {@code indentation} is outside
         *         the range {@code [0, 8]}
         */
        public Builder withIndentation(final int indentation) {
            template().indentation = validateIndentation(indentation);
            return this;
        }

        /**
         * Configures SQL operators and keywords to be generated using uppercase letters.
         *
         * @return this builder
         */
        public Builder uppercaseForKeywords() {
            template().uppercaseForKeywords = true;
            return this;
        }

        /**
         * Configures SQL operators and keywords to be generated using lowercase letters.
         *
         * @return this builder
         */
        public Builder lowercaseForKeywords() {
            template().uppercaseForKeywords = false;
            return this;
        }

        /**
         * Configures generated index names to include the {@code without_nulls} part
         * when applicable.
         *
         * @return this builder
         */
        public Builder nameWithoutNulls() {
            template().nameWithoutNulls = true;
            return this;
        }

        /**
         * Configures generated index names not to include the {@code without_nulls} part.
         *
         * @return this builder
         */
        public Builder doNotNameWithoutNulls() {
            template().nameWithoutNulls = false;
            return this;
        }

        /**
         * Sets the position of the {@code idx} marker in generated index names.
         *
         * @param idxPosition position of the {@code idx} marker;
         *                    must not be {@code null}
         * @return this builder
         * @throws NullPointerException if {@code idxPosition} is {@code null}
         */
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
