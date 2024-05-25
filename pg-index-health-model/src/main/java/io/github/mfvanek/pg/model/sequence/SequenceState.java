/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.sequence;

import io.github.mfvanek.pg.model.DbObject;
import io.github.mfvanek.pg.model.validation.Validators;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

/**
 * A representation of a sequence's state in a database.
 * This class encapsulates the details of a database sequence, including its name, data type,
 * and the percentage of remaining values before it overflows.
 *
 * @author Blohny
 * @since 0.11.2
 */
@Immutable
public class SequenceState implements DbObject {

    private final String sequenceName;
    private final String dataType;
    private final double remainingPercentage;

    /**
     * Constructs a {@code SequenceState} object.
     *
     * @param sequenceName        sequence name; should be non-blank.
     * @param dataType            data type; should be non-blank.
     * @param remainingPercentage remaining percentage; in the range from 0 to 100 inclusive.
     */
    private SequenceState(@Nonnull final String sequenceName,
                          @Nonnull final String dataType,
                          final double remainingPercentage
    ) {
        this.sequenceName = Validators.notBlank(sequenceName, "sequenceName");
        this.dataType = Validators.notBlank(dataType, "dataType");
        this.remainingPercentage = Validators.validPercent(remainingPercentage, "remainingPercentage");
    }

    /**
     * Returns the name of the sequence.
     *
     * @return the name of the sequence.
     */
    @Nonnull
    public String getSequenceName() {
        return sequenceName;
    }

    /**
     * Returns the data type of the sequence.
     *
     * @return the data type of the sequence.
     */
    @Nonnull
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
    @Nonnull
    @Override
    public final String getName() {
        return sequenceName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof SequenceState)) {
            return false;
        }

        final SequenceState that = (SequenceState) other;
        return Objects.equals(sequenceName, that.sequenceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode() {
        return Objects.hash(sequenceName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return SequenceState.class.getSimpleName() + '{' +
            "sequenceName='" + sequenceName + '\'' +
            ", dataType='" + dataType + '\'' +
            ", remainingPercentage=" + remainingPercentage +
            '}';
    }

    /**
     * Constructs a {@code SequenceState} object.
     *
     * @param sequenceName        sequence name; should be non-blank.
     * @param dataType            data type; should be non-blank.
     * @param remainingPercentage remaining percentage; in the range from 0 to 100 inclusive.
     * @return {@code SequenceState}
     */
    @Nonnull
    public static SequenceState of(@Nonnull final String sequenceName,
                                   @Nonnull final String dataType,
                                   final double remainingPercentage) {
        return new SequenceState(sequenceName, dataType, remainingPercentage);
    }
}
