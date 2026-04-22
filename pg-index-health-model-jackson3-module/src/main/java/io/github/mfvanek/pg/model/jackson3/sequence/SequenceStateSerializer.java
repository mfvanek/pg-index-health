/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.sequence;

import io.github.mfvanek.pg.model.sequence.SequenceNameAware;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

/**
 * A custom JSON serializer for the {@link SequenceState} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class SequenceStateSerializer extends ValueSerializer<SequenceState> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final SequenceState value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        gen.writeStringProperty(SequenceNameAware.SEQUENCE_NAME_FIELD, value.getSequenceName());
        gen.writeStringProperty(SequenceState.DATA_TYPE_FIELD, value.getDataType());
        gen.writeNumberProperty(SequenceState.REMAINING_PERCENTAGE_FIELD, value.getRemainingPercentage());
        gen.writeEndObject();
    }
}
