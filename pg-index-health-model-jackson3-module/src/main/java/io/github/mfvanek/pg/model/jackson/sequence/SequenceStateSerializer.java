/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.sequence;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.sequence.SequenceNameAware;
import io.github.mfvanek.pg.model.sequence.SequenceState;

import java.io.IOException;

/**
 * A custom JSON serializer for the {@link SequenceState} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class SequenceStateSerializer extends JsonSerializer<SequenceState> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final SequenceState value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(SequenceNameAware.SEQUENCE_NAME_FIELD, value.getSequenceName());
        gen.writeStringField(SequenceState.DATA_TYPE_FIELD, value.getDataType());
        gen.writeNumberField(SequenceState.REMAINING_PERCENTAGE_FIELD, value.getRemainingPercentage());
        gen.writeEndObject();
    }
}
