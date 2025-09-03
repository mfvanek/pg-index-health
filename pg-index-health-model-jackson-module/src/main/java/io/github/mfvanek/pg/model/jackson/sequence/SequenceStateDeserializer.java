/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.sequence;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.mfvanek.pg.model.jackson.common.ModelDeserializer;
import io.github.mfvanek.pg.model.sequence.SequenceNameAware;
import io.github.mfvanek.pg.model.sequence.SequenceState;

import java.io.IOException;

/**
 * A deserializer for {@link SequenceState} objects, enabling JSON deserialization into immutable {@code SequenceState} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class SequenceStateDeserializer extends ModelDeserializer<SequenceState> {

    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceState deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
        final JsonNode node = p.getCodec().readTree(p);
        final String sequenceName = getStringField(ctxt, node, SequenceNameAware.SEQUENCE_NAME_FIELD);
        final String dataType = getStringField(ctxt, node, SequenceState.DATA_TYPE_FIELD);
        final double remainingPercentage = node.get(SequenceState.REMAINING_PERCENTAGE_FIELD).asDouble();
        return SequenceState.of(sequenceName, dataType, remainingPercentage);
    }
}
