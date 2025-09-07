/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson.support;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mfvanek.pg.model.jackson.PgIndexHealthModelModule;

public abstract class ObjectMapperTestBase {

    protected final ObjectMapper objectMapper = prepareObjectMapper();

    private static ObjectMapper prepareObjectMapper() {
        final JsonFactory factory = new JsonFactory();
        factory.disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
        factory.enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION.mappedFeature());
        final ObjectMapper mapper = new ObjectMapper(factory);
        mapper.registerModule(new PgIndexHealthModelModule());
        return mapper;
    }
}
