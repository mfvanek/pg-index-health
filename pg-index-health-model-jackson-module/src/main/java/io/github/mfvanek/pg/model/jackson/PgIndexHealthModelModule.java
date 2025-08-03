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
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.AnyObject;
import io.github.mfvanek.pg.model.jackson.column.ColumnDeserializer;
import io.github.mfvanek.pg.model.jackson.column.ColumnSerializer;
import io.github.mfvanek.pg.model.jackson.column.ColumnWithSerialTypeDeserializer;
import io.github.mfvanek.pg.model.jackson.column.ColumnWithSerialTypeSerializer;
import io.github.mfvanek.pg.model.jackson.context.PgContextDeserializer;
import io.github.mfvanek.pg.model.jackson.context.PgContextSerializer;
import io.github.mfvanek.pg.model.jackson.dbobject.AnyObjectDeserializer;
import io.github.mfvanek.pg.model.jackson.dbobject.AnyObjectSerializer;
import io.github.mfvanek.pg.model.jackson.generated.ModuleVersion;

import java.io.Serial;

@SuppressWarnings("checkstyle:ClassDataAbstractionCoupling")
public class PgIndexHealthModelModule extends SimpleModule {

    @Serial
    private static final long serialVersionUID = 1L;

    public PgIndexHealthModelModule() {
        super(PgIndexHealthModelModule.class.getSimpleName(), ModuleVersion.VERSION);

        addSerializer(PgContext.class, new PgContextSerializer());
        addDeserializer(PgContext.class, new PgContextDeserializer());

        addSerializer(AnyObject.class, new AnyObjectSerializer());
        addDeserializer(AnyObject.class, new AnyObjectDeserializer());

        addSerializer(Column.class, new ColumnSerializer());
        addDeserializer(Column.class, new ColumnDeserializer());

        addSerializer(ColumnWithSerialType.class, new ColumnWithSerialTypeSerializer());
        addDeserializer(ColumnWithSerialType.class, new ColumnWithSerialTypeDeserializer());
    }
}
