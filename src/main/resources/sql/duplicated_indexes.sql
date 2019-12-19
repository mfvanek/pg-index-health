select table_name,
    string_agg('idx=' || idx::text || ', size=' || pg_relation_size(idx), '; ') as duplicated_indexes
from (
    select x.indexrelid::regclass as idx,
        x.indrelid::regclass as table_name,
        (x.indrelid::text || ' ' || x.indclass::text || ' ' || x.indkey::text || ' ' ||
         coalesce(pg_get_expr(x.indexprs, x.indrelid), '') || e' ' ||
         coalesce(pg_get_expr(x.indpred, x.indrelid), '')) as key
    from pg_catalog.pg_index x
             join pg_catalog.pg_stat_all_indexes psai on x.indexrelid = psai.indexrelid
    where psai.schemaname = 'public'::text
) sub
group by table_name, key
having count(*) > 1
order by table_name, sum(pg_relation_size(idx)) desc;
