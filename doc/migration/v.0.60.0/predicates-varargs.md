## Breaking Changes — `ofName` of predicates renamed to `ofNames`

### What changed

The `ofName` factory methods of the name-based predicates have been renamed to `ofNames`
and now take one or more names:

| Predicate                       | New signatures                                                          |
|---------------------------------|-------------------------------------------------------------------------|
| `SkipTablesByNamePredicate`     | `ofNames(String, String...)`, `ofNames(PgContext, String, String...)`   |
| `SkipIndexesByNamePredicate`    | `ofNames(String, String...)`, `ofNames(PgContext, String, String...)`   |
| `SkipBySequenceNamePredicate`   | `ofNames(String, String...)`, `ofNames(PgContext, String, String...)`   |
| `SkipDbObjectsByNamePredicate`  | `ofNames(String, String...)`, `ofNames(PgContext, String, String...)`   |
| `SkipByColumnNamePredicate`     | `ofNames(String, String...)`                                            |
| `SkipByConstraintNamePredicate` | `ofNames(String, String...)`                                            |

Every passed name must be non-null and non-blank. Duplicates are allowed and ignored.
The `Collection`-based factories (`of(...)`, `ofDefault(...)`) are kept.

### Migration

Replace `ofName` with `ofNames`. Several exclusions of the same kind can now be passed in one call.

```java
// Before
SkipTablesByNamePredicate.ofName("accounts")
SkipTablesByNamePredicate.ofName(ctx, "accounts").and(SkipTablesByNamePredicate.ofName(ctx, "clients"))
SkipTablesByNamePredicate.of(ctx, List.of("accounts", "clients"))

// After
SkipTablesByNamePredicate.ofNames("accounts")
SkipTablesByNamePredicate.ofNames(ctx, "accounts", "clients")
SkipTablesByNamePredicate.ofNames(ctx, "accounts", "clients")
```
