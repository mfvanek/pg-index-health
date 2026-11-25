## Breaking Changes — `Column` now carries its type

### What changed

**`Column` has a mandatory `columnType` field.**

`ColumnWithType` has been removed: its type is now part of `Column` itself.
The type is a `String` exactly as PostgreSQL returns it (`atttypid::regtype::text`, without typmod),
e.g. `integer`, `character varying`, `timestamp with time zone`, `integer[]`.

All factory methods of `Column` require the type. It can be passed as a `String` or as a `PgColumnType` constant.
For array columns use `PgColumnType.getArrayType()`, e.g. `PgColumnType.TEXT.getArrayType()` returns `text[]`
(serial pseudo-types have no array form and throw `UnsupportedOperationException`).

**`Column.ofNotNull` and `Column.ofNullable` require a `PgContext`.**

The overloads without `PgContext` have been removed. Use `PgContext.ofDefault()` for tables in the `public` schema
(the table name is left unchanged). `Column.of(tableName, columnName, notNull, columnType)` without context is kept
for already qualified table names.

For the most common types there are shortcut factories (all with `PgContext`):
`ofBigint`, `ofInteger`, `ofVarchar`, `ofText`, `ofCharacter`, `ofBoolean`, `ofTimestamp`, `ofTimestamptz`, `ofUuid`, `ofDate` (not null)
and `ofNullableBigint`, `ofNullableInteger`, `ofNullableVarchar`, `ofNullableText`, `ofNullableCharacter`, `ofNullableBoolean`,
`ofNullableTimestamp`, `ofNullableTimestamptz`, `ofNullableUuid`, `ofNullableDate` (nullable).

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
Column.ofNullable(ctx, "orders", "tags", PgColumnType.INTEGER.getArrayType())
Column.of("public.orders", "tags", false, "integer[]")
```

The same applies to the convenience factories that create a column internally:
`ForeignKey.ofNotNull/ofNullable`, `IndexWithColumns.ofNotNull/ofNullable` and
`TableWithColumns.ofNotNull/ofNullable` take an additional `columnType` argument (`String` or `PgColumnType`).

**`ForeignKey` and `TableWithColumns`: `ofNotNullColumn` / `ofNullableColumn` have been renamed to `ofNotNull` / `ofNullable`**
(the same names as in `IndexWithColumns`).

```java
// Before
TableWithColumns.ofNotNullColumn(ctx, "orders", "id")
ForeignKey.ofNullableColumn(ctx, "orders", "fk_orders_client", "client_id")

// After
TableWithColumns.ofNotNull(ctx, "orders", "id", PgColumnType.BIGINT)
ForeignKey.ofNullable(ctx, "orders", "fk_orders_client", "client_id", "bigint")
```

**`ForeignKey.ofNotNull`, `ForeignKey.ofNullable` and `ForeignKey.ofColumn` require a `PgContext`.**

The overloads without `PgContext` have been removed; use `PgContext.ofDefault()` for tables in the `public` schema
(the table name is left unchanged). `ForeignKey.of(tableName, constraintName, columns)` without context is kept
for already qualified table names, as well as `ForeignKey.of(constraint, columns)`.

```java
// Before
ForeignKey.ofNotNullColumn("orders", "fk_orders_client", "client_id")
ForeignKey.ofColumn("orders", "fk_orders_client", column)

// After
ForeignKey.ofNotNull(PgContext.ofDefault(), "orders", "fk_orders_client", "client_id", PgColumnType.BIGINT)
ForeignKey.ofNullable(ctx, "orders", "fk_orders_client", "client_id", "bigint")
ForeignKey.of(ctx, "orders", "fk_orders_client", column)
ForeignKey.ofBigint(ctx, "orders", "fk_orders_client", "client_id")
```

`ForeignKey` also has typed shortcut factories (all with `PgContext`) for the same types as `Column`:
`ofBigint`, `ofInteger`, ..., `ofDate` (not null) and `ofNullableBigint`, `ofNullableInteger`, ..., `ofNullableDate` (nullable),
e.g. `ForeignKey.ofInteger(ctx, "orders", "fk_orders_client", "client_id")` instead of
`ForeignKey.ofNotNull(ctx, "orders", "fk_orders_client", "client_id", PgColumnType.INTEGER)`.

**`IndexWithColumns`: creating an index with a single column requires a `PgContext` or an `Index`.**

`IndexWithColumns.ofSingle(tableName, indexName, indexSizeInBytes, column)` without context has been removed;
use `PgContext.ofDefault()` for tables in the `public` schema. `IndexWithColumns.of(tableName, indexName, indexSizeInBytes, columns)`
without context is kept for already qualified table names.

`IndexWithColumns` also has typed shortcut factories (all with `PgContext`, zero index size) for the same types as `Column`:
`ofBigint`, `ofInteger`, ..., `ofDate` (not null) and `ofNullableBigint`, `ofNullableInteger`, ..., `ofNullableDate` (nullable).

```java
// Before
IndexWithColumns.ofSingle("orders", "i_orders_client_id", 0L, column)
IndexWithColumns.ofNullable(ctx, "clients", "i_clients_middle_name", "middle_name", PgColumnType.VARCHAR)

// After
IndexWithColumns.of(PgContext.ofDefault(), "orders", "i_orders_client_id", column)
IndexWithColumns.ofNullableVarchar(ctx, "clients", "i_clients_middle_name", "middle_name")
```

**Factories with several columns accept them as varargs; `ofSingle`, `ofColumn` and `ofColumns` have been renamed to `of`.**

`ForeignKey`, `IndexWithColumns` and `TableWithColumns` have `of(..., Column column, Column... otherColumns)` factories,
so the columns no longer need to be wrapped in a `List`. At least one column is required at compile time.
The factories that take a `List<Column>` are kept for collections built at runtime.

- `ForeignKey.ofColumn(...)` → `ForeignKey.of(...)`
- `IndexWithColumns.ofSingle(...)` and `IndexWithColumns.ofColumns(...)` → `IndexWithColumns.of(...)`
  (a zero-size overload without `indexSizeInBytes` is available for both varargs and `List`)
- `TableWithColumns.ofSingle(table, column)` → `TableWithColumns.of(table, column)`

```java
// Before
ForeignKey.of(ctx, "orders", "fk_orders_client", List.of(Column.ofBigint(ctx, "orders", "client_id"), Column.ofBigint(ctx, "orders", "region_id")))
IndexWithColumns.ofColumns(ctx, "orders", "i_orders_client_id_region_id", 0L, List.of(clientId, regionId))
IndexWithColumns.ofSingle(ctx, "orders", "i_orders_client_id", 0L, clientId)
TableWithColumns.ofSingle(Table.of(ctx, "orders"), clientId)

// After
ForeignKey.of(ctx, "orders", "fk_orders_client", Column.ofBigint(ctx, "orders", "client_id"), Column.ofBigint(ctx, "orders", "region_id"))
IndexWithColumns.of(ctx, "orders", "i_orders_client_id_region_id", clientId, regionId)
IndexWithColumns.of(ctx, "orders", "i_orders_client_id", clientId)
TableWithColumns.of(Table.of(ctx, "orders"), clientId)
```

`TableWithColumns` also has typed shortcut factories (all with `PgContext`) for the same types as `Column`:
`ofBigint`, `ofInteger`, ..., `ofDate` (not null) and `ofNullableBigint`, `ofNullableInteger`, ..., `ofNullableDate` (nullable),
e.g. `TableWithColumns.ofBigint(ctx, "orders", "id")` instead of `TableWithColumns.ofNotNull(ctx, "orders", "id", PgColumnType.BIGINT)`.

**`ColumnTypeAware` has been merged into `ColumnNameAware`.**

`ColumnTypeAware` has been removed. `getColumnType()` and the `COLUMN_FIELD` / `COLUMN_TYPE_FIELD` constants
are now part of `ColumnNameAware`. Replace `ColumnTypeAware` with `ColumnNameAware` in your code.

**`PgColumnType` replaces `SerialType`.**

`SerialType` has been removed. Use `PgColumnType.SMALL_SERIAL`, `PgColumnType.SERIAL` and `PgColumnType.BIG_SERIAL` instead.
`PgColumnType.isSerial()` tells whether a type is one of these serial pseudo-types.

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
Checks that return `columns` arrays expect every element in the format `name,not_null,type`,
e.g. `quote_ident(col.attname) || ',' || col.attnotnull::text || ',' || col.atttypid::regtype::text`.
An element in the old `name,not_null` format causes an `IllegalArgumentException` when the check returns at least one row.

Known limitation: the element is split by its last two commas, so a quoted column name may contain a comma,
but the column type must not. A user-defined type whose quoted name contains a comma (e.g. `"st,atus"`) is parsed incorrectly.

**Checks.**

- All `columns_with_*` checks now return `Column` instead of `ColumnWithType`.
- `btree_indexes_on_array_columns` returns one row per index with all its columns (previously one row per array column).
  Indexes that have array columns only in `INCLUDE (...)` are no longer reported.
  Since the result contains all columns of the index, an exclusion by column name (`SkipByColumnNamePredicate`)
  now hides the whole index if any of its columns matches, including columns that are not arrays.
  For example, skipping `account_number` hides the index on `(account_number, roles)` even though `roles` is the array column.

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
