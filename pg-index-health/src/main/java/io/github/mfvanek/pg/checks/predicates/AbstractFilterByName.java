/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.checks.predicates;

import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

/**
 * Base class for filters by name.
 *
 * @author Ivan Vakhrushev
 * @since 0.6.0
 */
abstract class AbstractFilterByName {

    protected final Set<String> exclusions;

    protected AbstractFilterByName(@Nonnull final Collection<String> exclusions) {
        this.exclusions = Objects.requireNonNull(exclusions, "exclusions cannot be null")
            .stream()
            .map(s -> s.toLowerCase(Locale.ROOT))
            .collect(Collectors.toUnmodifiableSet());
    }

    protected AbstractFilterByName(@Nonnull final String objectName) {
        this(Set.of(Validators.notBlank(objectName, "objectName")));
    }
}
