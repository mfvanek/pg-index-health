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
import io.github.mfvanek.pg.model.jackson3.common.ModelDeserializer;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;

/**
 * A deserializer for {@link AnyObject} objects, enabling JSON deserialization into immutable {@code AnyObject} instances.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.0
 */
public class AnyObjectDeserializer extends ModelDeserializer<AnyObject> {

    /**
     * {@inheritDoc}
     */
    @Override
    public AnyObject deserialize(final JsonParser p, final DeserializationContext ctxt) {
        final JsonNode rootNode = ctxt.readTree(p);
        final String objectName = getStringField(ctxt, rootNode, AnyObject.OBJECT_NAME_FIELD);
        final String objectType = getStringField(ctxt, rootNode, AnyObject.OBJECT_TYPE_FIELD);
        return AnyObject.ofRaw(objectName, objectType);
    }
}
