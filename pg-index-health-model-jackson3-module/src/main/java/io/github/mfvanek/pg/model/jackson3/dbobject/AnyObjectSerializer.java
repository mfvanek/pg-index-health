/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.dbobject;

import io.github.mfvanek.pg.model.dbobject.AnyObject;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.util.Locale;

/**
 * A custom JSON serializer for the {@link AnyObject} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class AnyObjectSerializer extends ValueSerializer<AnyObject> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final AnyObject value, final JsonGenerator gen, final SerializationContext ctxt) {
        gen.writeStartObject();
        gen.writeStringProperty(AnyObject.OBJECT_NAME_FIELD, value.getName());
        gen.writeStringProperty(AnyObject.OBJECT_TYPE_FIELD, value.getObjectType().name().toLowerCase(Locale.ROOT));
        gen.writeEndObject();
    }
}
