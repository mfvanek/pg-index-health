## Breaking Changes — `Column` now carries its type

### What changed

**`Column` has a mandatory `columnType` field.**

`ColumnWithType` has been removed: its type is now part of `Column` itself.
The type is a `String` exactly as PostgreSQL returns it (`atttypid::regtype::text`, without typmod),
e.g. `integer`, `character varying`, `timestamp with time zone`, `integer[]`.

All factory methods of `Column` require the type. It can be passed as a `String` or as a `PgColumnType` constant.

**`Column.ofNotNull` and `Column.ofNullable` require a `PgContext`.**

The overloads without `PgContext` have been removed. Use `PgContext.ofDefault()` for tables in the `public` schema
(the table name is left unchanged). `Column.of(tableName, columnName, notNull, columnType)` without context is kept
for already qualified table names.

For the most common types there are shortcut factories (all with `PgContext`):
`ofBigint`, `ofInteger`, `ofVarchar`, `ofText`, `ofCharacter`, `ofBoolean`, `ofTimestamp`, `ofTimestamptz`, `ofUuid` (not null)
and `ofNullableBigint`, `ofNullableInteger`, `ofNullableVarchar`, `ofNullableText`, `ofNullableCharacter`, `ofNullableBoolean`,
`ofNullableTimestamp`, `ofNullableTimestamptz`, `ofNullableUuid` (nullable).

```java
// Before
Column.ofNotNull(ctx, "orders", "id")
Column.ofNullable("orders", "comment")
ColumnWithType.ofBigint(Column.ofNotNull(ctx, "orders", "id"))
ColumnWithType.of(Column.ofNotNull(ctx, "orders", "status"), ctx.enrichWithSchema("order_status"))

// After
Column.ofBigint(ctx, "orders", "id")
Column.ofNullableText(PgContext.ofDefault(), "orders", "comment")
Column.ofNotNull(ctx, "orders", "id", PgColumnType.BIGINT)
Column.ofNotNull(ctx, "orders", "status", ctx.enrichWithSchema("order_status"))
Column.of(ctx, "orders", "tags", false, "integer[]")
Column.of("public.orders", "tags", false, "integer[]")
```

The same applies to the convenience factories that create a column internally:
`ForeignKey.ofNotNullColumn/ofNullableColumn`, `IndexWithColumns.ofNotNull/ofNullable` and
`TableWithColumns.ofNotNullColumn/ofNullableColumn` take an additional `columnType` argument (`String` or `PgColumnType`).

**`ColumnTypeAware` has been merged into `ColumnNameAware`.**

`ColumnTypeAware` has been removed. `getColumnType()` and the `COLUMN_FIELD` / `COLUMN_TYPE_FIELD` constants
are now part of `ColumnNameAware`. Replace `ColumnTypeAware` with `ColumnNameAware` in your code.

**`PgColumnType` replaces `SerialType`.**

`SerialType` has been removed. Use `PgColumnType.SMALL_SERIAL`, `PgColumnType.SERIAL` and `PgColumnType.BIG_SERIAL` instead.

**`ColumnWithSerialType` takes the serial type from the column type.**

```java
// Before
ColumnWithSerialType.of(column, SerialType.SERIAL, "t_id_seq")

// After
ColumnWithSerialType.of(ctx, "t", "id", true, PgColumnType.SERIAL, "t_id_seq")
ColumnWithSerialType.of(ctx, "t", "id", false, "serial", "t_id_seq")
```

`ColumnWithSerialType.of(PgContext, tableName, columnName, notNull, columnType, sequenceName)` creates the column inside;
the column type can be passed as a `String` or as a `PgColumnType` and must be `smallserial`, `serial` or `bigserial`.
`ColumnWithSerialType.of(column, sequenceName)` and `ColumnWithSerialType.of(PgContext, column, sequenceName)` are kept.

`getSerialType()` now returns `PgColumnType`.

The `ofSerial`, `ofBigSerial` and `ofSmallSerial` factories now require a `PgContext`, take the table name and column name
instead of a `Column` and always create a not null column of `serial`, `bigserial` or `smallserial` type respectively.
The overloads without `PgContext` have been removed; use `PgContext.ofDefault()` for tables in the `public` schema:

```java
// Before
ColumnWithSerialType.ofBigSerial(ctx, Column.ofNotNull(ctx, "t", "id"), "t_id_seq")
ColumnWithSerialType.ofSerial(column, "t_id_seq")

// After
ColumnWithSerialType.ofBigSerial(ctx, "t", "id", "t_id_seq")
ColumnWithSerialType.ofSerial(PgContext.ofDefault(), "t", "id", "t_id_seq")
```

**Extractors.**

`ColumnWithTypeExtractor`, `IndexWithSingleColumnExtractor` and `IndexWithNullableColumnExtractor` have been removed.
`ColumnExtractor` now reads the `column_type` column, so the SQL query of a custom check that uses `ColumnExtractor`
must return `table_name, column_not_null, column_type, column_name`.
Checks that return `columns` arrays expect every element in the format `name,not_null,type`.

**Checks.**

- All `columns_with_*` checks now return `Column` instead of `ColumnWithType`.
- `btree_indexes_on_array_columns` returns one row per index with all its columns (previously one row per array column).
  Indexes that have array columns only in `INCLUDE (...)` are no longer reported.

**JSON (Jackson 2 and Jackson 3 modules).**

- `Column` is serialized with an additional `columnType` property.
- `ColumnWithSerialType` is serialized as `{"column": {...}, "sequenceName": "..."}`; the `columnType` and `serialType` properties have been removed
  (the type is inside `column`).
- Serializer and deserializer for `ColumnWithType` have been removed.

---

### Migration guide for custom checks

```java
// Before
public class AllDateTimeColumnsShouldEndWithAtCheckOnHost extends AbstractCheckOnHost<ColumnWithType> {

    public AllDateTimeColumnsShouldEndWithAtCheckOnHost(final PgConnection pgConnection) {
        super(ColumnWithType.class, pgConnection, StandardCheckInfo.ofStatic("ALL_DATE_TIME_COLUMNS_SHOULD_END_WITH_AT", SQL), ColumnWithTypeExtractor.of());
    }
}

// After
public class AllDateTimeColumnsShouldEndWithAtCheckOnHost extends AbstractCheckOnHost<Column> {

    public AllDateTimeColumnsShouldEndWithAtCheckOnHost(final PgConnection pgConnection) {
        super(Column.class, pgConnection, StandardCheckInfo.ofStatic("ALL_DATE_TIME_COLUMNS_SHOULD_END_WITH_AT", SQL), ColumnExtractor.of());
    }
}
```
