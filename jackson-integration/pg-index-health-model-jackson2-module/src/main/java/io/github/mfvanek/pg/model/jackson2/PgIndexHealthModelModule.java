/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson2;

import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.column.ColumnWithType;
import io.github.mfvanek.pg.model.constraint.Constraint;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.AnyObject;
import io.github.mfvanek.pg.model.function.StoredFunction;
import io.github.mfvanek.pg.model.index.DuplicatedIndexes;
import io.github.mfvanek.pg.model.index.Index;
import io.github.mfvanek.pg.model.index.IndexWithBloat;
import io.github.mfvanek.pg.model.index.IndexWithColumns;
import io.github.mfvanek.pg.model.index.UnusedIndex;
import io.github.mfvanek.pg.model.jackson2.column.ColumnDeserializer;
import io.github.mfvanek.pg.model.jackson2.column.ColumnSerializer;
import io.github.mfvanek.pg.model.jackson2.column.ColumnWithSerialTypeDeserializer;
import io.github.mfvanek.pg.model.jackson2.column.ColumnWithSerialTypeSerializer;
import io.github.mfvanek.pg.model.jackson2.column.ColumnWithTypeDeserializer;
import io.github.mfvanek.pg.model.jackson2.column.ColumnWithTypeSerializer;
import io.github.mfvanek.pg.model.jackson2.constraint.ConstraintDeserializer;
import io.github.mfvanek.pg.model.jackson2.constraint.ConstraintSerializer;
import io.github.mfvanek.pg.model.jackson2.constraint.DuplicatedForeignKeysDeserializer;
import io.github.mfvanek.pg.model.jackson2.constraint.DuplicatedForeignKeysSerializer;
import io.github.mfvanek.pg.model.jackson2.constraint.ForeignKeyDeserializer;
import io.github.mfvanek.pg.model.jackson2.constraint.ForeignKeySerializer;
import io.github.mfvanek.pg.model.jackson2.context.PgContextDeserializer;
import io.github.mfvanek.pg.model.jackson2.context.PgContextSerializer;
import io.github.mfvanek.pg.model.jackson2.dbobject.AnyObjectDeserializer;
import io.github.mfvanek.pg.model.jackson2.dbobject.AnyObjectSerializer;
import io.github.mfvanek.pg.model.jackson2.function.StoredFunctionDeserializer;
import io.github.mfvanek.pg.model.jackson2.function.StoredFunctionSerializer;
import io.github.mfvanek.pg.model.jackson.generated.ModuleVersion;
import io.github.mfvanek.pg.model.jackson2.index.DuplicatedIndexesDeserializer;
import io.github.mfvanek.pg.model.jackson2.index.DuplicatedIndexesSerializer;
import io.github.mfvanek.pg.model.jackson2.index.IndexDeserializer;
import io.github.mfvanek.pg.model.jackson2.index.IndexSerializer;
import io.github.mfvanek.pg.model.jackson2.index.IndexWithBloatDeserializer;
import io.github.mfvanek.pg.model.jackson2.index.IndexWithBloatSerializer;
import io.github.mfvanek.pg.model.jackson2.index.IndexWithColumnsDeserializer;
import io.github.mfvanek.pg.model.jackson2.index.IndexWithColumnsSerializer;
import io.github.mfvanek.pg.model.jackson2.index.UnusedIndexDeserializer;
import io.github.mfvanek.pg.model.jackson2.index.UnusedIndexSerializer;
import io.github.mfvanek.pg.model.jackson2.sequence.SequenceStateDeserializer;
import io.github.mfvanek.pg.model.jackson2.sequence.SequenceStateSerializer;
import io.github.mfvanek.pg.model.jackson2.table.TableDeserializer;
import io.github.mfvanek.pg.model.jackson2.table.TableSerializer;
import io.github.mfvanek.pg.model.jackson2.table.TableWithBloatDeserializer;
import io.github.mfvanek.pg.model.jackson2.table.TableWithBloatSerializer;
import io.github.mfvanek.pg.model.jackson2.table.TableWithColumnsDeserializer;
import io.github.mfvanek.pg.model.jackson2.table.TableWithColumnsSerializer;
import io.github.mfvanek.pg.model.jackson2.table.TableWithMissingIndexDeserializer;
import io.github.mfvanek.pg.model.jackson2.table.TableWithMissingIndexSerializer;
import io.github.mfvanek.pg.model.sequence.SequenceState;
import io.github.mfvanek.pg.model.table.Table;
import io.github.mfvanek.pg.model.table.TableWithBloat;
import io.github.mfvanek.pg.model.table.TableWithColumns;
import io.github.mfvanek.pg.model.table.TableWithMissingIndex;

import java.io.Serial;

/**
 * Provides a custom Jackson module to support the serialization and deserialization of PostgreSQL-related entities.
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity", "checkstyle:ExecutableStatementCount"})
public class PgIndexHealthModelModule extends SimpleModule {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new instance of the PgIndexHealthModelModule and initializes custom serializers and deserializers
     * for various PostgreSQL health-related model types.
     */
    public PgIndexHealthModelModule() {
        super(PgIndexHealthModelModule.class.getSimpleName(), ModuleVersion.VERSION);

        addSerializer(PgContext.class, new PgContextSerializer());
        addDeserializer(PgContext.class, new PgContextDeserializer());

        addSerializer(AnyObject.class, new AnyObjectSerializer());
        addDeserializer(AnyObject.class, new AnyObjectDeserializer());

        addSerializer(Column.class, new ColumnSerializer());
        addDeserializer(Column.class, new ColumnDeserializer());

        addSerializer(ColumnWithType.class, new ColumnWithTypeSerializer());
        addDeserializer(ColumnWithType.class, new ColumnWithTypeDeserializer());

        addSerializer(ColumnWithSerialType.class, new ColumnWithSerialTypeSerializer());
        addDeserializer(ColumnWithSerialType.class, new ColumnWithSerialTypeDeserializer());

        addSerializer(Constraint.class, new ConstraintSerializer());
        addDeserializer(Constraint.class, new ConstraintDeserializer());

        addSerializer(ForeignKey.class, new ForeignKeySerializer());
        addDeserializer(ForeignKey.class, new ForeignKeyDeserializer());

        addSerializer(DuplicatedForeignKeys.class, new DuplicatedForeignKeysSerializer());
        addDeserializer(DuplicatedForeignKeys.class, new DuplicatedForeignKeysDeserializer());

        addSerializer(StoredFunction.class, new StoredFunctionSerializer());
        addDeserializer(StoredFunction.class, new StoredFunctionDeserializer());

        addSerializer(Index.class, new IndexSerializer());
        addDeserializer(Index.class, new IndexDeserializer());

        addSerializer(IndexWithBloat.class, new IndexWithBloatSerializer());
        addDeserializer(IndexWithBloat.class, new IndexWithBloatDeserializer());

        addSerializer(IndexWithColumns.class, new IndexWithColumnsSerializer());
        addDeserializer(IndexWithColumns.class, new IndexWithColumnsDeserializer());

        addSerializer(UnusedIndex.class, new UnusedIndexSerializer());
        addDeserializer(UnusedIndex.class, new UnusedIndexDeserializer());

        addSerializer(DuplicatedIndexes.class, new DuplicatedIndexesSerializer());
        addDeserializer(DuplicatedIndexes.class, new DuplicatedIndexesDeserializer());

        addSerializer(SequenceState.class, new SequenceStateSerializer());
        addDeserializer(SequenceState.class, new SequenceStateDeserializer());

        addSerializer(Table.class, new TableSerializer());
        addDeserializer(Table.class, new TableDeserializer());

        addSerializer(TableWithBloat.class, new TableWithBloatSerializer());
        addDeserializer(TableWithBloat.class, new TableWithBloatDeserializer());

        addSerializer(TableWithColumns.class, new TableWithColumnsSerializer());
        addDeserializer(TableWithColumns.class, new TableWithColumnsDeserializer());

        addSerializer(TableWithMissingIndex.class, new TableWithMissingIndexSerializer());
        addDeserializer(TableWithMissingIndex.class, new TableWithMissingIndexDeserializer());
    }
}
