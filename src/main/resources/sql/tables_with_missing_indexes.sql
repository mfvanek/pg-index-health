with tables_without_indexes as (
    select relname::text as table_name,
        coalesce(seq_scan, 0) - coalesce(idx_scan, 0) as too_much_seq,
        pg_relation_size(relname::regclass) as table_size,
        coalesce(seq_scan, 0) as seq_scan,
        coalesce(idx_scan, 0) as idx_scan
    from pg_stat_all_tables
    where schemaname = 'public'::text
      and pg_relation_size(relname::regclass) > 5::integer * 8192 /*skip small tables*/
)
select table_name,
    seq_scan,
    idx_scan
from tables_without_indexes
where (seq_scan + idx_scan) > 100::integer /*table in use*/
  and too_much_seq > 0 /*too much sequential scans*/
order by table_name, too_much_seq desc;
