with foreign_key_indexes as (
    select i.indexrelid
    from pg_constraint c
             join lateral unnest(c.conkey) with ordinality as u(attnum, attposition) on true
             join pg_index i on i.indrelid = c.conrelid and (c.conkey::int[] <@ indkey::int[])
    where c.contype = 'f'
)
select psui.relname as table_name,
    psui.indexrelname as index_name,
    pg_relation_size(i.indexrelid) as index_size,
    psui.idx_scan as index_scans
from pg_stat_user_indexes psui
         join pg_index i on psui.indexrelid = i.indexrelid
where psui.schemaname = 'public'::text
  and not i.indisunique
  and i.indexrelid not in (select * from foreign_key_indexes) /*retain indexes on foreign keys*/
  and psui.idx_scan < 50::integer
  and pg_relation_size(psui.relid) >= 5::integer * 8192 /*skip small tables*/
  and pg_relation_size(psui.indexrelid) >= 5::integer * 8192 /*skip small indexes*/
order by psui.relname, pg_relation_size(i.indexrelid) desc;
