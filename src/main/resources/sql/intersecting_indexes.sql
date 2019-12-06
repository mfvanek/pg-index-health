select a.indrelid::regclass as table_name,
                        'idx=' || a.indexrelid::regclass || ', size=' || pg_relation_size(a.indexrelid) || '; idx=' ||
                        b.indexrelid::regclass || ', size=' || pg_relation_size(b.indexrelid) as intersected_indexes
from (
    select *, array_to_string(indkey, ' ') as cols
    from pg_index) as a
         join (select *, array_to_string(indkey, ' ') as cols from pg_index) as b
              on (a.indrelid = b.indrelid and a.indexrelid > b.indexrelid and (
                      (a.cols like b.cols || '%' and coalesce(substr(a.cols, length(b.cols) + 1, 1), ' ') = ' ') or
                      (b.cols like a.cols || '%' and coalesce(substr(b.cols, length(a.cols) + 1, 1), ' ') = ' ')))
order by a.indrelid::regclass::text;
