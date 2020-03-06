select x.indrelid::regclass as table_name,
    x.indexrelid::regclass as index_name,
    string_agg(a.attname, ', ') as nullable_fields,
    pg_relation_size(x.indexrelid) as index_size
from pg_catalog.pg_index x
         join pg_catalog.pg_stat_all_indexes psai on x.indexrelid = psai.indexrelid
         join pg_catalog.pg_attribute a ON a.attrelid = x.indrelid AND a.attnum = any (x.indkey)
where not x.indisunique
  and not a.attnotnull
  and psai.schemaname = :schema_name_param::text
  and array_position(x.indkey, a.attnum) = 0 /*only for first segment*/
  and (x.indpred is null or (position(lower(a.attname) in lower(pg_get_expr(x.indpred, x.indrelid))) = 0))
group by x.indrelid, x.indexrelid, x.indpred
order by table_name, index_name;
