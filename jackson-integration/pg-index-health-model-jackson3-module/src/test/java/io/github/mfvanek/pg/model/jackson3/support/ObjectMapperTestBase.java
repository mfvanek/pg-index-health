/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson3.support;

import io.github.mfvanek.pg.model.jackson3.PgIndexHealthModelModule;
import tools.jackson.core.StreamReadFeature;
import tools.jackson.core.StreamWriteFeature;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.json.JsonMapper;

public abstract class ObjectMapperTestBase {

    protected final JsonMapper objectMapper = prepareObjectMapper();

    private static JsonMapper prepareObjectMapper() {
        final JsonFactory factory = JsonFactory.builder()
            .disable(StreamWriteFeature.AUTO_CLOSE_CONTENT)
            .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION)
            .build();

        return JsonMapper.builder(factory)
            .addModule(new PgIndexHealthModelModule())
            .build();
    }
}
