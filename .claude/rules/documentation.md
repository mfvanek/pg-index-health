# Rules for check documentation

Applies to: `doc/eng/` and `doc/rus/`

## File naming and location (DOCUMENTATION_FILES_MUST_MATCH_CHECK_NAME)

Every check must have exactly two documentation files — one in English and one in Russian:

- `doc/eng/<check_name>.md`
- `doc/rus/<check_name>.md`

The file name must match the SQL file name (without the `.sql` extension) and the `Diagnostic` enum entry name (lowercased, underscores preserved).

Use `doc/eng/foreign_keys_with_null_values.md` and `doc/rus/foreign_keys_with_null_values.md` as templates.

## Required sections (DOCUMENTATION_MUST_HAVE_ALL_SECTIONS)

Both language versions must contain these sections in order:

1. Top-level heading — a human-readable description of what the check detects
2. Body — explanation of the problem and why it matters
3. `## SQL query` / `## SQL запрос` — a single bullet linking to the SQL file on GitHub:
   `https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/<check_name>.sql`
4. `## Check type` / `## Тип проверки` — exactly one of the two values described below
5. `## Support for partitioned tables` / `## Поддержка секционированных таблиц` — whether and how
6. `## Reproduction script` / `## Скрипт для воспроизведения` — a fenced `sql` code block (may be left empty initially)
7. `## How to fix` / `## Как исправить` — actionable remediation advice

## Determining the check type (DOCUMENTATION_CHECK_TYPE_MUST_BE_CORRECT)

Use **exactly one** of these two values:

### static

```
- **static** (can be performed on an empty database in component/integration tests)
```

Russian:
```
- **static** (может выполняться на пустой БД в компонентных\интеграционных тестах)
```

A check is **static** when its SQL query relies solely on schema structure stored in `pg_catalog` DDL tables
(`pg_class`, `pg_attribute`, `pg_constraint`, `pg_index`, `pg_namespace`, etc.) and produces correct results
on a freshly created, never-populated database — no VACUUM, ANALYZE, or data loading required.

### runtime

```
- **runtime** (requires accumulated statistics)
```

Russian:
```
- **runtime** (требует накопленной статистики)
```

A check is **runtime** when its SQL query depends on values that are only accurate after data has been written
and statistics have been collected. The key signals:

- Uses `relpages` or `reltuples` from `pg_class` — these fields are updated by VACUUM and ANALYZE; on a fresh
  table they may be 0 even when data exists, or remain > 0 after rows are deleted until VACUUM runs.
- Joins against `pg_stat_*` views (`pg_stat_user_tables`, `pg_stat_user_indexes`, `pg_stat_all_indexes`, etc.)
- Results change after VACUUM or ANALYZE is executed
- The SQL comment mentions "accumulated statistics", "requires VACUUM", or references `pg_stat_*`

When in doubt: if the check behavior depends on whether VACUUM has been run, classify it as **runtime**.
