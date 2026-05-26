# Check for columns that use large object types (`oid` or `lo`)

Columns with type `oid` or `lo` store a handle (an object identifier) pointing to a large object
managed by PostgreSQL's large-object facility (`pg_largeobject`).
Every read or write to such a column requires an additional round-trip to `pg_largeobject`,
which adds I/O overhead and complexity compared to inline storage.

The `lo` type is a domain over `oid` provided by the `lo` extension.
It was created to work around the fact that PostgreSQL's foreign-key machinery does not automatically
clean up orphaned large objects when referencing rows are deleted.
Even with the `lo` extension's trigger-based cleanup, the storage model remains indirect and harder to reason about.

For most use cases, `bytea` (for binary data) or `text` (for character data) are better alternatives:
they store data inline in the table's TOAST storage, benefit from standard PostgreSQL compression,
and do not require special cleanup handling.

See the [official large-objects documentation](https://www.postgresql.org/docs/current/largeobjects.html)
and the [lo extension documentation](https://www.postgresql.org/docs/current/lo.html).

## SQL query

- [columns_with_blob_type.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/columns_with_blob_type.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Supports partitioned tables.
The check is performed on the partitioned table itself (the parent one). Individual sections (descendants) are ignored.

## Reproduction script

```sql
create schema if not exists demo;

-- lo domain type requires the extension (installs the 'lo' domain over oid)
create extension if not exists lo;

-- raw oid column (large object handle)
create table if not exists demo."document-bad" (
    id bigserial primary key,
    title text not null,
    "content-bad" oid not null
);

-- lo domain column; trigger is best practice but not required for detection
create table if not exists demo.image (
    id bigserial primary key,
    title text not null,
    raster lo
);

create or replace trigger t_raster
    before update or delete on demo.image
    for each row execute function lo_manage(raster);

-- two blob columns on the same table → two rows in the result set
create table if not exists demo.media_file (
    id bigserial primary key,
    name text not null,
    thumbnail oid,
    full_image lo not null
);

create or replace trigger t_full_image
    before update or delete on demo.media_file
    for each row execute function lo_manage(full_image);

-- partitioned
create table if not exists demo.attachment (
    id bigserial not null,
    created_at date not null,
    file_data oid
) partition by range (created_at);

create table if not exists demo.attachment_2024
    partition of demo.attachment for values from ('2024-01-01') to ('2025-01-01');

-- bytea is the recommended replacement — stored inline, no pg_largeobject reads
create table if not exists demo.binary_data (
    id bigserial primary key,
    title text not null,
    content bytea not null
);
```

## How to fix

Replace `oid` or `lo` columns with `bytea` for binary data or `text`/`varchar` for character data.

```sql
alter table your_schema.your_table
    alter column your_column type bytea using null; -- migrate existing large objects separately
```

If you must keep large objects for a legacy reason, document why and ensure the `lo` extension's
cleanup trigger is in place to prevent orphaned entries in `pg_largeobject`.
