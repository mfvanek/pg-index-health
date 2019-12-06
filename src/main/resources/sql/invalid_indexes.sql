select
    x.indrelid::regclass as table_name,
    x.indexrelid::regclass as index_name
from pg_index x
join pg_stat_all_indexes psai on x.indexrelid = psai.indexrelid and psai.schemaname = 'public'::text
where x.indisvalid = false;
