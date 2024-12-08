/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.index;

import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IndexWithColumnsTest {

    @Test
    void gettersShouldWork() {
        final Column column = Column.ofNullable("t", "f");
        final IndexWithColumns index = IndexWithColumns.ofSingle("t", "i", 11L, column);
        assertThat(index.getTableName()).isEqualTo("t");
        assertThat(index.getIndexName())
            .isEqualTo("i")
            .isEqualTo(index.getName());
        assertThat(index.getIndexSizeInBytes()).isEqualTo(11L);
        assertThat(index.getColumns())
            .hasSize(1)
            .isUnmodifiable()
            .containsExactly(Column.ofNullable("t", "f"));
        assertThat(index.getObjectType())
            .isEqualTo(PgObjectType.INDEX);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidArguments() {
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
        assertThatThrownBy(() -> IndexWithColumns.ofSingle("t", "i", 0, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("column cannot be null");
        assertThatThrownBy(() -> IndexWithColumns.ofColumns("t", "i", 0, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columns cannot be null");
    }

    @Test
    void tableShouldBeTheSame() {
        final Column column = Column.ofNullable("t2", "f");
        assertThatThrownBy(() -> IndexWithColumns.ofSingle("t", "i", 1L, column))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Table name is not the same within given rows");
    }

    @Test
    void testToString() {
        assertThat(IndexWithColumns.ofSingle("t", "i", 22L, Column.ofNullable("t", "f")))
            .hasToString("IndexWithColumns{tableName='t', indexName='i', indexSizeInBytes=22, columns=[Column{tableName='t', columnName='f', notNull=false}]}");
        final PgContext ctx = PgContext.of("tst");
        final Column column = Column.ofNullable(ctx, "t", "f");
        assertThat(IndexWithColumns.ofSingle(ctx, "t", "i", 22L, column))
            .hasToString("IndexWithColumns{tableName='tst.t', indexName='tst.i', indexSizeInBytes=22, columns=[Column{tableName='tst.t', columnName='f', notNull=false}]}");
        assertThat(IndexWithColumns.ofColumns(ctx, "t", "i", 22L, List.of(column)))
            .hasToString("IndexWithColumns{tableName='tst.t', indexName='tst.i', indexSizeInBytes=22, columns=[Column{tableName='tst.t', columnName='f', notNull=false}]}");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final Column column = Column.ofNullable("t1", "f");
        final IndexWithColumns first = IndexWithColumns.ofSingle("t1", "i1", 1, column);
        final IndexWithColumns theSame = IndexWithColumns.ofSingle("t1", "i1", 3, column); // different size!
        final IndexWithColumns second = IndexWithColumns.ofSingle("t2", "i2", 2, Column.ofNotNull("t2", "f2"));
        final List<Column> columns = List.of(
            Column.ofNullable("t3", "t"),
            Column.ofNullable("t3", "f"));
        final IndexWithColumns third = IndexWithColumns.ofColumns("t3", "i3", 2, columns);

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
        assertThat(anotherType)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(IndexWithColumns.class)
            .withIgnoredFields("indexSizeInBytes", "columns")
            .verify();
    }
}
