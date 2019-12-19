select x.indrelid::regclass as table_name,
    x.indexrelid::regclass as index_name
from pg_catalog.pg_index x
         join pg_catalog.pg_stat_all_indexes psai on x.indexrelid = psai.indexrelid
where psai.schemaname = 'public'::text
  and x.indisvalid = false;
