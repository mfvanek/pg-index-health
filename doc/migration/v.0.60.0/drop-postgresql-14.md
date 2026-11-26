## Breaking Changes — PostgreSQL 14 is no longer supported

### What changed

PostgreSQL 14 reached its [end of life](https://www.postgresql.org/support/versioning/) on November 12, 2026.
Starting from this release the minimal supported PostgreSQL version is **15**.

The following methods were removed from `PostgresVersionAware`, `PostgresVersionHolder`
and `PostgreSqlContainerWrapper` (module `pg-index-health-testing`)
because they always return `true` on PostgreSQL 15 and higher:

| Removed method                            | Replacement           |
|-------------------------------------------|-----------------------|
| `isCumulativeStatisticsSystemSupported()` | assume `true`         |
| `isUnloggedSequencesSupported()`          | assume `true`         |

`getMountVolume()` and `isNotNullConstraintsSupported()` are kept.

### Migration

Upgrade your databases to PostgreSQL 15 or higher.
Remove calls to the removed methods and keep only the code from the `true` branch.

```java
// Before
if (container.isUnloggedSequencesSupported()) {
    // ...
}

// After
// ...
```
