select pt.tablename as table_name
from pg_catalog.pg_tables pt
where pt.schemaname = 'public'::text
  and pt.tablename not in (
    select c.conrelid::regclass::text as table_name
    from pg_catalog.pg_constraint c
    where c.contype = 'p')
order by pt.tablename;
