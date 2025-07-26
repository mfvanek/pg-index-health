/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;

public class PgIndexHealthModelModule extends SimpleModule {

    public PgIndexHealthModelModule() {
        super(PgIndexHealthModelModule.class.getSimpleName(), ModuleVersion.VERSION);
    }
}
