## Predicates accept several names in `ofName`

### What changed

The `ofName` factory methods of the name-based predicates now take one or more names:

| Predicate                       | New signatures                                                                       |
|---------------------------------|--------------------------------------------------------------------------------------|
| `SkipTablesByNamePredicate`     | `ofName(String, String...)`, `ofName(PgContext, String, String...)`                 |
| `SkipIndexesByNamePredicate`    | `ofName(String, String...)`, `ofName(PgContext, String, String...)`                 |
| `SkipBySequenceNamePredicate`   | `ofName(String, String...)`, `ofName(PgContext, String, String...)`                 |
| `SkipDbObjectsByNamePredicate`  | `ofName(String, String...)`, `ofName(PgContext, String, String...)`                 |
| `SkipByColumnNamePredicate`     | `ofName(String, String...)`                                                          |
| `SkipByConstraintNamePredicate` | `ofName(String, String...)`                                                          |

Every passed name must be non-null and non-blank. Duplicates are allowed and ignored.
The `Collection`-based factories (`of(...)`, `ofDefault(...)`) are kept.

### Migration

The change is **source compatible**: existing calls such as `ofName("t")` and `ofName(ctx, "t")` compile without changes.
It is **not binary compatible**: code compiled against an earlier version must be recompiled.

```java
// Before
SkipTablesByNamePredicate.of(ctx, List.of("accounts", "clients"))
SkipTablesByNamePredicate.ofName(ctx, "accounts").and(SkipTablesByNamePredicate.ofName(ctx, "clients"))

// After
SkipTablesByNamePredicate.ofName(ctx, "accounts", "clients")
```
