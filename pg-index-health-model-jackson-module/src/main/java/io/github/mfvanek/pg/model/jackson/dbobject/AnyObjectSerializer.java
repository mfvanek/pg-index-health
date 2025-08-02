/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.dbobject;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.github.mfvanek.pg.model.dbobject.AnyObject;

import java.io.IOException;
import java.util.Locale;

/**
 * A custom JSON serializer for the {@link AnyObject} class.
 *
 * @author Ivan Vakhrushev
 * @since 0.20.3
 */
public class AnyObjectSerializer extends JsonSerializer<AnyObject> {

    /**
     * {@inheritDoc}
     */
    @Override
    public void serialize(final AnyObject value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(AnyObject.OBJECT_NAME_FIELD, value.getName());
        gen.writeStringField(AnyObject.OBJECT_TYPE_FIELD, value.getObjectType().name().toLowerCase(Locale.ROOT));
        gen.writeEndObject();
    }
}
