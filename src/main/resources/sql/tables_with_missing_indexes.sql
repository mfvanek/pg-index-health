with tables_without_indexes as (
    select psat.relname::text as table_name,
        pg_table_size(psat.relid) as table_size,
        coalesce(psat.seq_scan, 0) - coalesce(psat.idx_scan, 0) as too_much_seq,
        coalesce(psat.seq_scan, 0) as seq_scan,
        coalesce(psat.idx_scan, 0) as idx_scan
    from pg_catalog.pg_stat_all_tables psat
    where psat.schemaname = ?::text
)
select table_name,
    table_size,
    seq_scan,
    idx_scan
from tables_without_indexes
where (seq_scan + idx_scan) > 100::integer /*table in use*/
  and too_much_seq > 0 /*too much sequential scans*/
order by table_name, too_much_seq desc;
