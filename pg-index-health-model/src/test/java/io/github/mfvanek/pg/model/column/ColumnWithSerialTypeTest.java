/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.column;

import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.PgObjectType;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ColumnWithSerialTypeTest {

    @Test
    void gettersShouldWork() {
        final PgContext ctx = PgContext.ofDefault();
        final ColumnWithSerialType column = prepare();
        assertThat(column)
            .isNotNull()
            .satisfies(c -> {
                assertThat(c.getTableName())
                    .isEqualTo("t1");
                assertThat(c.getColumnName())
                    .isEqualTo("c1")
                    .isEqualTo(c.getName());
                assertThat(c.isNullable())
                    .isFalse();
                assertThat(c.isNotNull())
                    .isTrue();
                assertThat(c.getColumnType())
                    .isEqualTo("serial");
                assertThat(c.getSerialType())
                    .isEqualTo(PgColumnType.SERIAL);
                assertThat(c.getSequenceName())
                    .isEqualTo("s1");
                assertThat(c.getObjectType())
                    .isEqualTo(PgObjectType.TABLE);
                assertThat(c.toColumn())
                    .isEqualTo(Column.ofNotNull(ctx, "t1", "c1", PgColumnType.SERIAL));
            });

        final ColumnWithSerialType theSameButNullable = prepareNullable();
        assertThat(theSameButNullable)
            .isNotNull()
            .satisfies(c -> {
                assertThat(c.getTableName())
                    .isEqualTo("t1");
                assertThat(c.getColumnName())
                    .isEqualTo("c1")
                    .isEqualTo(c.getName());
                assertThat(c.isNullable())
                    .isTrue();
                assertThat(c.isNotNull())
                    .isFalse();
                assertThat(c.getSerialType())
                    .isEqualTo(PgColumnType.SERIAL);
                assertThat(c.getSequenceName())
                    .isEqualTo("s1");
                assertThat(c.toColumn())
                    .isEqualTo(Column.ofNullable(ctx, "t1", "c1", PgColumnType.SERIAL));
            });

        assertThat(ColumnWithSerialType.of(ctx, "t1", "c1", true, "smallserial", "s1").getSerialType())
            .isEqualTo(PgColumnType.SMALL_SERIAL);
        assertThat(ColumnWithSerialType.of(ctx, "t1", "c1", true, "bigserial", "s1").getSerialType())
            .isEqualTo(PgColumnType.BIG_SERIAL);
    }

    @Test
    void factoriesWithContextShouldWork() {
        final PgContext ctx = PgContext.of("tst");
        assertThat(ColumnWithSerialType.of(ctx, "t2", "c2", false, PgColumnType.BIG_SERIAL, "s2"))
            .usingRecursiveComparison()
            .isEqualTo(ColumnWithSerialType.of(Column.of("tst.t2", "c2", false, "bigserial"), "tst.s2"));
        assertThat(ColumnWithSerialType.of(ctx, "t2", "c2", false, "smallserial", "s2"))
            .usingRecursiveComparison()
            .isEqualTo(ColumnWithSerialType.of(Column.of("tst.t2", "c2", false, "smallserial"), "tst.s2"));
        assertThat(ColumnWithSerialType.ofBigSerial(ctx, "t2", "c2", "s2"))
            .usingRecursiveComparison()
            .isEqualTo(ColumnWithSerialType.of(Column.of("tst.t2", "c2", true, "bigserial"), "tst.s2"));
        assertThat(ColumnWithSerialType.ofSerial(ctx, "t2", "c2", "s2"))
            .usingRecursiveComparison()
            .isEqualTo(ColumnWithSerialType.of(Column.of("tst.t2", "c2", true, "serial"), "tst.s2"));
        assertThat(ColumnWithSerialType.ofSmallSerial(ctx, "t2", "c2", "s2"))
            .usingRecursiveComparison()
            .isEqualTo(ColumnWithSerialType.of(Column.of("tst.t2", "c2", true, "smallserial"), "tst.s2"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void withInvalidValuesShouldThrowException() {
        final PgContext ctx = PgContext.ofDefault();
        assertThatThrownBy(() -> ColumnWithSerialType.of(null, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("column cannot be null");

        final Column column = Column.ofNullable(ctx, "t1", "c1", PgColumnType.BIG_SERIAL);
        assertThatThrownBy(() -> ColumnWithSerialType.of(column, null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("sequenceName cannot be null");

        assertThatThrownBy(() -> ColumnWithSerialType.of(column, ""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("sequenceName cannot be blank");

        assertThatThrownBy(() -> ColumnWithSerialType.of(column, "   "))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("sequenceName cannot be blank");

        final Column notSerial = Column.ofBigint(ctx, "t1", "c1");
        assertThatThrownBy(() -> ColumnWithSerialType.of(notSerial, "s1"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("column type must be serial but was 'bigint'");

        assertThatThrownBy(() -> ColumnWithSerialType.of(ctx, "t1", "c1", true, (PgColumnType) null, "s1"))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("columnType cannot be null");

        final Column unknownType = Column.ofNotNull(ctx, "t1", "c1", "unknown");
        assertThatThrownBy(() -> ColumnWithSerialType.of(unknownType, "s1"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("pgColumnType = 'unknown'");
    }

    @Test
    void toStringTest() {
        assertThat(prepare())
            .hasToString("""
                ColumnWithSerialType{column=Column{tableName='t1', columnName='c1', notNull=true, columnType='serial'}, \
                sequenceName='s1'}""");

        final PgContext ctx = PgContext.of("tst");
        final Column col = Column.ofNotNull(ctx, "t2", "c2", PgColumnType.SMALL_SERIAL);
        assertThat(ColumnWithSerialType.of(ctx, col, "s2"))
            .hasToString("""
                ColumnWithSerialType{column=Column{tableName='tst.t2', columnName='c2', notNull=true, columnType='smallserial'}, \
                sequenceName='tst.s2'}""");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void testEqualsAndHashCode() {
        final PgContext ctx = PgContext.ofDefault();
        final ColumnWithSerialType first = prepare();
        final ColumnWithSerialType theSame = prepare();
        final ColumnWithSerialType theSameButNullable = prepareNullable();
        final ColumnWithSerialType second = ColumnWithSerialType.of(ctx, "t1", "c2", true, PgColumnType.SERIAL, "s1");
        final ColumnWithSerialType third = ColumnWithSerialType.of(ctx, "t1", "c1", true, PgColumnType.SMALL_SERIAL, "s1");
        final ColumnWithSerialType forth = ColumnWithSerialType.of(ctx, "t1", "c1", true, PgColumnType.SERIAL, "s2");

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

        // nullability is an attribute, not a part of identity
        assertThat(theSameButNullable)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // others
        assertThat(second)
            .isNotEqualTo(first)
            .doesNotHaveSameHashCodeAs(first);

        // serial type is an attribute, not a part of identity
        assertThat(third)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);

        // sequence name is an attribute, not a part of identity
        assertThat(forth)
            .isEqualTo(first)
            .hasSameHashCodeAs(first);
    }

    @Test
    void equalsHashCodeShouldAdhereContracts() {
        EqualsVerifier.forClass(ColumnWithSerialType.class)
            .withIgnoredFields(ColumnWithSerialType.SEQUENCE_NAME_FIELD)
            .verify();
    }

    @Test
    void compareToTest() {
        final PgContext ctx = PgContext.ofDefault();
        final ColumnWithSerialType first = prepare();
        final ColumnWithSerialType theSame = prepare();
        final ColumnWithSerialType theSameButNullable = prepareNullable();
        final ColumnWithSerialType theSameButAnotherType = ColumnWithSerialType.of(ctx, "t1", "c1", true, PgColumnType.BIG_SERIAL, "s1");
        final ColumnWithSerialType theSameButAnotherSequence = ColumnWithSerialType.of(ctx, "t1", "c1", true, PgColumnType.SERIAL, "s2");
        final ColumnWithSerialType second = ColumnWithSerialType.of(ctx, "t1", "c2", true, PgColumnType.SERIAL, "s1");
        final ColumnWithSerialType third = ColumnWithSerialType.of(ctx, "t2", "c1", true, PgColumnType.SERIAL, "s1");

        // noinspection ConstantConditions
        assertThatThrownBy(() -> first.compareTo(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessage("other cannot be null");

        assertThat(first)
            .isEqualByComparingTo(first) // self
            .isEqualByComparingTo(theSame) // the same
            .isEqualByComparingTo(theSameButNullable) // nullability is not a part of identity
            .isEqualByComparingTo(theSameButAnotherType) // serial type is not a part of identity
            .isEqualByComparingTo(theSameButAnotherSequence) // sequence name is not a part of identity
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
    private static ColumnWithSerialType prepare() {
        final PgContext ctx = PgContext.ofDefault();
        return ColumnWithSerialType.of(ctx, "t1", "c1", true, PgColumnType.SERIAL, "s1");
    }

    @NonNull
    private static ColumnWithSerialType prepareNullable() {
        final PgContext ctx = PgContext.ofDefault();
        return ColumnWithSerialType.of(ctx, "t1", "c1", false, PgColumnType.SERIAL, "s1");
    }
}
