select psat.relname::text as table_name,
    pg_table_size(psat.relid) as table_size
from pg_catalog.pg_stat_all_tables psat
where psat.schemaname = ?::text
  and psat.relname not in (
    select c.conrelid::regclass::text as table_name
    from pg_catalog.pg_constraint c
    where c.contype = 'p')
order by psat.relname::text;
