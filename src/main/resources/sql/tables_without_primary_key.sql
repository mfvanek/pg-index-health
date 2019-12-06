select tablename as table_name
from pg_tables
where schemaname = 'public'::text
  and tablename not in (
    select c.conrelid::regclass::text as table_name
    from pg_constraint c
    where contype = 'p')
order by tablename;
