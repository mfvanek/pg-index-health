/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.sequence;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;

/**
 * An immutable representation of a sequence's state in a database.
 * This class encapsulates the details of a database sequence, including its name, data type,
 * and the percentage of remaining values before it overflows.
 *
 * @author Blohny
 * @since 0.12.0
 */
public final class SequenceState implements DbObject, SequenceNameAware {

    /**
     * Represents the field name for storing the data type of the sequence.
     */
    public static final String DATA_TYPE_FIELD = "dataType";
    /**
     * Represents the field name for storing the remaining percentage of the sequence value.
     */
    public static final String REMAINING_PERCENTAGE_FIELD = "remainingPercentage";

    private final String sequenceName;
    private final String dataType;
    private final double remainingPercentage;

    /**
     * Constructs a {@code SequenceState} object.
     *
     * @param sequenceName        sequence name; should be non-blank.
     * @param dataType            data type; should be non-blank.
     * @param remainingPercentage the remaining percentage; in the range from 0 to 100 inclusive.
     */
    private SequenceState(final String sequenceName,
                          final String dataType,
                          final double remainingPercentage
    ) {
        this.sequenceName = Validators.notBlank(sequenceName, SEQUENCE_NAME_FIELD);
        this.dataType = Validators.notBlank(dataType, DATA_TYPE_FIELD);
        this.remainingPercentage = Validators.validPercent(remainingPercentage, REMAINING_PERCENTAGE_FIELD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSequenceName() {
        return sequenceName;
    }

    /**
     * Returns the data type of the sequence.
     *
     * @return the data type of the sequence.
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Returns the remaining percentage of the sequence.
     *
     * @return the remaining percentage of the sequence.
     */
    public double getRemainingPercentage() {
        return remainingPercentage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getSequenceName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PgObjectType getObjectType() {
        return PgObjectType.SEQUENCE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof final SequenceState that)) {
            return false;
        }

        return Objects.equals(sequenceName, that.sequenceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(sequenceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return SequenceState.class.getSimpleName() + '{' +
            SEQUENCE_NAME_FIELD + "='" + sequenceName + '\'' +
            ", " + DATA_TYPE_FIELD + "='" + dataType + '\'' +
            ", " + REMAINING_PERCENTAGE_FIELD + '=' + remainingPercentage +
            '}';
    }

    /**
     * Constructs a {@code SequenceState} object.
     *
     * @param sequenceName        sequence name; should be non-blank.
     * @param dataType            data type; should be non-blank.
     * @param remainingPercentage the remaining percentage; in the range from 0 to 100 inclusive.
     * @return {@code SequenceState}
     */
    public static SequenceState of(final String sequenceName,
                                   final String dataType,
                                   final double remainingPercentage) {
        return new SequenceState(sequenceName, dataType, remainingPercentage);
    }

    /**
     * Constructs a {@code SequenceState} object with given context.
     *
     * @param pgContext           the schema context to enrich sequence name; must be non-null.
     * @param sequenceName        sequence name; should be non-blank.
     * @param dataType            data type; should be non-blank.
     * @param remainingPercentage the remaining percentage; in the range from 0 to 100 inclusive.
     * @return {@code SequenceState}
     * @since 0.14.3
     */
    public static SequenceState of(final PgContext pgContext,
                                   final String sequenceName,
                                   final String dataType,
                                   final double remainingPercentage) {
        return of(PgContext.enrichWith(sequenceName, pgContext), dataType, remainingPercentage);
    }
}
