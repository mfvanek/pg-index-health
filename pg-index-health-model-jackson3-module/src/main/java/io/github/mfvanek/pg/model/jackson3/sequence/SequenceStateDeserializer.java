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

import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import io.github.mfvanek.pg.model.sequence.SequenceNameAware;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

/**
 * A deserializer for {@link SequenceState} objects, enabling JSON deserialization into immutable {@code SequenceState} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class SequenceStateDeserializer extends ModelDeserializer<SequenceState> {

    /**
     * {@inheritDoc}
     */
    @Override
    public SequenceState deserialize(final JsonParser p, final DeserializationContext ctxt) {
        final JsonNode rootNode = ctxt.readTree(p);
        final String sequenceName = getStringField(ctxt, rootNode, SequenceNameAware.SEQUENCE_NAME_FIELD);
        final String dataType = getStringField(ctxt, rootNode, SequenceState.DATA_TYPE_FIELD);
        final double remainingPercentage = getDoubleField(ctxt, rootNode, SequenceState.REMAINING_PERCENTAGE_FIELD);
        return SequenceState.of(sequenceName, dataType, remainingPercentage);
    }
}
