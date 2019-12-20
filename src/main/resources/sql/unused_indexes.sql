with foreign_key_indexes as (
    select i.indexrelid
    from pg_catalog.pg_constraint c
             join lateral unnest(c.conkey) with ordinality as u(attnum, attposition) on true
             join pg_catalog.pg_index i on i.indrelid = c.conrelid and (c.conkey::int[] <@ i.indkey::int[])
    where c.contype = 'f'
)
select psui.relname as table_name,
    psui.indexrelname as index_name,
    pg_relation_size(i.indexrelid) as index_size,
    psui.idx_scan as index_scans
from pg_catalog.pg_stat_user_indexes psui
         join pg_catalog.pg_index i on psui.indexrelid = i.indexrelid
where psui.schemaname = ?::text
  and not i.indisunique
  and i.indexrelid not in (select * from foreign_key_indexes) /*retain indexes on foreign keys*/
  and psui.idx_scan < 50::integer
order by psui.relname, pg_relation_size(i.indexrelid) desc;
