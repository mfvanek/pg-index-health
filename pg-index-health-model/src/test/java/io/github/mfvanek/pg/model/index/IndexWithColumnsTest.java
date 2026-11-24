/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnsAware;
import io.github.mfvanek.pg.model.column.PgColumnType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IndexWithColumnsTest {

    @Test
    void gettersShouldWork() {
        final PgContext ctx = PgContext.ofDefault();
        final Column column = Column.ofNullableInteger(ctx, "t", "f");
        final IndexWithColumns index = IndexWithColumns.ofSingle(ctx, "t", "i", 11L, column);
        assertThat(index.getTableName()).isEqualTo("t");
        assertThat(index.getIndexName())
            .isEqualTo("i")
            .isEqualTo(index.getName());
        assertThat(index.getIndexSizeInBytes()).isEqualTo(11L);
        assertThat(index.getColumns())
            .hasSize(1)
            .isUnmodifiable()
            .containsExactly(Column.ofNullableInteger(ctx, "t", "f"));
        assertThat(index.getObjectType())
            .isEqualTo(PgObjectType.INDEX);
        assertThat(index.getFirstColumn())
            .isEqualTo(Column.ofNullableInteger(ctx, "t", "f"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
        final PgContext ctx = PgContext.ofDefault();
        assertThatThrownBy(() -> IndexWithColumns.ofColumns(null, null, 0, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("tableName cannot be null");
        assertThatThrownBy(() -> IndexWithColumns.ofColumns("", null, 0, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableName cannot be blank");
        assertThatThrownBy(() -> IndexWithColumns.ofColumns("  ", null, 0, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("tableName cannot be blank");
        assertThatThrownBy(() -> IndexWithColumns.ofColumns("t", null, 0, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("indexName cannot be null");
        assertThatThrownBy(() -> IndexWithColumns.ofColumns("t", "", 0, null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("indexName cannot be blank");
        assertThatThrownBy(() -> IndexWithColumns.ofSingle(ctx, "t", "i", 0, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("column cannot be null");
        assertThatThrownBy(() -> IndexWithColumns.ofColumns("t", "i", 0, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columns cannot be null");
    }

    @Test
    void tableShouldBeTheSame() {
        final PgContext ctx = PgContext.ofDefault();
        final Column column = Column.ofNullableInteger(ctx, "t2", "f");
        assertThatThrownBy(() -> IndexWithColumns.ofSingle(ctx, "t", "i", 1L, column))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Table name is not the same within given rows");
    }

    @Test
    void testToString() {
        final PgContext ctx = PgContext.of("tst");
        final Column column = Column.ofNullableInteger(ctx, "t", "f");
        assertThat(IndexWithColumns.ofSingle(ctx, "t", "i", 22L, column))
            .hasToString("IndexWithColumns{tableName='tst.t', indexName='tst.i', indexSizeInBytes=22, columns=[Column{tableName='tst.t', columnName='f', notNull=false, columnType='integer'}]}");
        assertThat(IndexWithColumns.ofColumns(ctx, "t", "i", 22L, List.of(column)))
            .hasToString("IndexWithColumns{tableName='tst.t', indexName='tst.i', indexSizeInBytes=22, columns=[Column{tableName='tst.t', columnName='f', notNull=false, columnType='integer'}]}");
        assertThat(IndexWithColumns.ofColumns(ctx, "t", "i", List.of(column)))
            .hasToString("IndexWithColumns{tableName='tst.t', indexName='tst.i', indexSizeInBytes=0, columns=[Column{tableName='tst.t', columnName='f', notNull=false, columnType='integer'}]}");
        assertThat(IndexWithColumns.ofNullable(ctx, "t", "i", "c", "integer"))
            .hasToString("IndexWithColumns{tableName='tst.t', indexName='tst.i', indexSizeInBytes=0, columns=[Column{tableName='tst.t', columnName='c', notNull=false, columnType='integer'}]}");
        assertThat(IndexWithColumns.ofNotNull(ctx, "t", "i", "c", "integer"))
            .hasToString("IndexWithColumns{tableName='tst.t', indexName='tst.i', indexSizeInBytes=0, columns=[Column{tableName='tst.t', columnName='c', notNull=true, columnType='integer'}]}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final PgContext ctx = PgContext.ofDefault();
        final Column column = Column.ofNullableInteger(ctx, "t1", "f");
        final IndexWithColumns first = IndexWithColumns.ofSingle(ctx, "t1", "i1", 1, column);
        final IndexWithColumns theSame = IndexWithColumns.ofSingle(ctx, "t1", "i1", 3, column); // different size!
        final IndexWithColumns second = IndexWithColumns.ofSingle(ctx, "t2", "i2", 2, Column.ofInteger(ctx, "t2", "f2"));
        final IndexWithColumns third = createThird();

        assertThat(first.equals(null)).isFalse();
        //noinspection EqualsBetweenInconvertibleTypes
        assertThat(first.equals(Integer.MAX_VALUE)).isFalse();

        // self
        assertThat(first)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // the same
        assertThat(theSame)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // others
        assertThat(second)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        assertThat(third)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first)
            .isNotEqualTo(second)
            .doesNotHaveSameHashCodeAs(second);

        // another
        final Index anotherType = Index.of("t1", "i1");
        //noinspection AssertBetweenInconvertibleTypes
        assertThat(anotherType)
            .isNotEqualTo(first)
            .hasSameHashCodeAs(first);
    }

    @Test
    void factoriesWithEnumTypeShouldWork() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(IndexWithColumns.ofNotNull(ctx, "t", "i", "c", PgColumnType.BIGINT))
            .usingRecursiveComparison()
            .isEqualTo(IndexWithColumns.ofNotNull(ctx, "t", "i", "c", "bigint"));
        assertThat(IndexWithColumns.ofNullable(ctx, "t", "i", "c", PgColumnType.UUID))
            .usingRecursiveComparison()
            .isEqualTo(IndexWithColumns.ofNullable(ctx, "t", "i", "c", "uuid"));
    }

    @Test
    void typedFactoriesShouldWork() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(List.of(
            IndexWithColumns.ofBigint(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofNullableBigint(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofInteger(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofNullableInteger(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofVarchar(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofNullableVarchar(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofText(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofNullableText(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofCharacter(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofNullableCharacter(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofBoolean(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofNullableBoolean(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofTimestamp(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofNullableTimestamp(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofTimestamptz(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofNullableTimestamptz(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofUuid(ctx, "t1", "i1", "c1"),
            IndexWithColumns.ofNullableUuid(ctx, "t1", "i1", "c1")))
            .usingRecursiveFieldByFieldElementComparator()
            .containsExactly(
                IndexWithColumns.ofNotNull(ctx, "t1", "i1", "c1", "bigint"),
                IndexWithColumns.ofNullable(ctx, "t1", "i1", "c1", "bigint"),
                IndexWithColumns.ofNotNull(ctx, "t1", "i1", "c1", "integer"),
                IndexWithColumns.ofNullable(ctx, "t1", "i1", "c1", "integer"),
                IndexWithColumns.ofNotNull(ctx, "t1", "i1", "c1", "character varying"),
                IndexWithColumns.ofNullable(ctx, "t1", "i1", "c1", "character varying"),
                IndexWithColumns.ofNotNull(ctx, "t1", "i1", "c1", "text"),
                IndexWithColumns.ofNullable(ctx, "t1", "i1", "c1", "text"),
                IndexWithColumns.ofNotNull(ctx, "t1", "i1", "c1", "character"),
                IndexWithColumns.ofNullable(ctx, "t1", "i1", "c1", "character"),
                IndexWithColumns.ofNotNull(ctx, "t1", "i1", "c1", "boolean"),
                IndexWithColumns.ofNullable(ctx, "t1", "i1", "c1", "boolean"),
                IndexWithColumns.ofNotNull(ctx, "t1", "i1", "c1", "timestamp without time zone"),
                IndexWithColumns.ofNullable(ctx, "t1", "i1", "c1", "timestamp without time zone"),
                IndexWithColumns.ofNotNull(ctx, "t1", "i1", "c1", "timestamp with time zone"),
                IndexWithColumns.ofNullable(ctx, "t1", "i1", "c1", "timestamp with time zone"),
                IndexWithColumns.ofNotNull(ctx, "t1", "i1", "c1", "uuid"),
                IndexWithColumns.ofNullable(ctx, "t1", "i1", "c1", "uuid"));
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(IndexWithColumns.class)
            .withIgnoredFields(ColumnsAware.COLUMNS_FIELD)
            .verify();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void compareToTest() {
        final PgContext ctx = PgContext.ofDefault();
        final Column column = Column.ofNullableInteger(ctx, "t1", "f");
        final IndexWithColumns first = IndexWithColumns.ofSingle(ctx, "t1", "i1", 1, column);
        final IndexWithColumns theSame = IndexWithColumns.ofSingle(ctx, "t1", "i1", 3, column); // different size!
        final IndexWithColumns second = IndexWithColumns.ofSingle(ctx, "t2", "i2", 2, Column.ofInteger(ctx, "t2", "f2"));
        final IndexWithColumns third = createThird();

        assertThatThrownBy(() -> first.compareTo(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("other cannot be null");

        assertThat(first)
            .isEqualByComparingTo(first) // self
            .isEqualByComparingTo(theSame) // the same
            .isLessThan(second)
            .isLessThan(third);

        assertThat(second)
            .isGreaterThan(first)
            .isLessThan(third);

        assertThat(third)
            .isGreaterThan(first)
            .isGreaterThan(second);
    }

    @NonNull
    private static IndexWithColumns createThird() {
        final PgContext ctx = PgContext.ofDefault();
        final List<Column> columns = List.of(
            Column.ofNullableInteger(ctx, "t3", "t"),
            Column.ofNullableInteger(ctx, "t3", "f"));
        return IndexWithColumns.ofColumns("t3", "i3", 2L, columns);
    }
}
