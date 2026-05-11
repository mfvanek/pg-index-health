## Breaking Changes — `AbstractCheckOnHost` Constructor & `doCheck` Contract

### What changed

**`AbstractCheckOnHost` now takes a `ResultSetExtractor` in its constructor (4-arg constructor).**

The constructor signature has changed from:

```java
// Before
protected AbstractCheckOnHost(Class<T> type, PgConnection pgConnection, CheckInfo checkInfo)
```

to:

```java
// After
protected AbstractCheckOnHost(Class<T> type, PgConnection pgConnection, CheckInfo checkInfo, ResultSetExtractor<T> rowMapper)
```

The extractor is stored as `protected final ResultSetExtractor<T> rowMapper` and is available to all subclasses.

**`doCheck` is no longer `abstract`.**

Previously every subclass was required to override `doCheck`. Now the base class provides a default implementation:

```java
protected List<T> doCheck(PgContext pgContext) {
    return executeQuery(pgContext, rowMapper);
}
```

Subclasses only need to override `doCheck` if they require custom query execution logic (e.g., using Spring's `JdbcClient`).

---

### Migration guide for custom checks

**Case 1 — simple custom check (no custom `doCheck` logic)**

```java
// Before
public class MyCustomCheckOnHost extends AbstractCheckOnHost<Column> {

    public MyCustomCheckOnHost(PgConnection pgConnection) {
        super(Column.class, pgConnection, StandardCheckInfo.ofStatic("MY_CHECK", NamedParametersParser.parse(SQL)));
    }

    @Override
    protected List<Column> doCheck(PgContext pgContext) {
        return executeQuery(pgContext, ColumnExtractor.of());
    }
}
```

```java
// After — pass the extractor to super(), remove doCheck entirely
public class MyCustomCheckOnHost extends AbstractCheckOnHost<Column> {

    public MyCustomCheckOnHost(PgConnection pgConnection) {
        super(Column.class, pgConnection,
            StandardCheckInfo.ofStatic("MY_CHECK", NamedParametersParser.parse(SQL)),
            ColumnExtractor.of());  // <-- added as 4th argument
    }
    // doCheck override is no longer needed
}
```

**Case 2 — custom check with non-standard query execution (e.g., Spring `JdbcClient`)**

Pass the extractor to `super()` as the 4th argument, keep the `doCheck` override, and use the inherited `rowMapper` field instead of allocating a new extractor:

```java
public class MyCustomCheckOnHost extends AbstractCheckOnHost<TableWithColumns> {

    private final JdbcClient jdbcClient;

    public MyCustomCheckOnHost(PgConnection pgConnection, JdbcClient jdbcClient) {
        super(TableWithColumns.class, pgConnection,
            StandardCheckInfo.ofStatic("MY_CHECK", SQL),
            TableWithColumnsExtractor.of());  // <-- 4th argument
        this.jdbcClient = Objects.requireNonNull(jdbcClient, "jdbcClient cannot be null");
    }

    @Override
    protected List<TableWithColumns> doCheck(PgContext pgContext) {
        return jdbcClient.sql(checkInfo.getSqlQuery())
            .param("schema_name_param", pgContext.getSchemaName())
            .query(rowMapper::mapRow)  // use inherited rowMapper field
            .list();
    }
}
```

---

### Summary of required changes

| What you had                                                                     | What to do                                                                                   |
|----------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| 3-arg `super(type, conn, checkInfo)` call                                        | Add your extractor as the **4th argument**: `super(type, conn, checkInfo, MyExtractor.of())` |
| `doCheck` override that only calls `executeQuery(pgContext, SomeExtractor.of())` | **Delete the override** — the base class now does this automatically                         |
| `doCheck` override with custom logic (JdbcClient, etc.)                          | Keep the override, but use `rowMapper::mapRow` instead of creating a new extractor instance  |
