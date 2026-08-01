# Check for unlogged sequences

Unlogged sequences are not backed by the Write-Ahead Log (WAL), so their state is not replicated to standby servers
and will be reset automatically after a server crash.
Their current value is lost after a crash, which may cause duplicate key errors
when the sequence is used as a default value for a column.

See also https://www.postgresql.org/docs/current/sql-createsequence.html

## SQL query

- [unlogged_sequences.sql](https://github.com/mfvanek/pg-index-health-sql/blob/master/sql/unlogged_sequences.sql)

## Check type

- **static** (can be performed on an empty database in component/integration tests)

## Support for partitioned tables

Not applicable to partitioned tables (sequences are standalone objects).

## Reproduction script

```sql
create schema if not exists demo;

create unlogged sequence demo.unlogged_seq_1;
```

## How to fix

Convert the unlogged sequence to a regular (logged) sequence:

```sql
alter sequence demo.unlogged_seq_1 set logged;
```
