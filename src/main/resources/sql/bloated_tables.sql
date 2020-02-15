-- Based on
-- https://wiki.postgresql.org/wiki/Show_database_bloat
-- https://github.com/dataegret/pg-utils/blob/master/sql/table_bloat.sql
-- https://github.com/pgexperts/pgx_scripts/blob/master/bloat/table_bloat_check.sql
-- https://github.com/ioguix/pgsql-bloat-estimation/blob/master/table/table_bloat.sql
with tables_stats as (
    SELECT
        pc.oid AS tblid, pc.relname AS tblname, pc.reltuples,
        pc.relpages AS heappages, coalesce(toast.relpages, 0) AS toastpages,
        coalesce(toast.reltuples, 0) AS toasttuples,
        coalesce(substring(
                         array_to_string(pc.reloptions, ' ')
                         FROM 'fillfactor=([0-9]+)')::smallint, 100) AS fillfactor,
        current_setting('block_size')::numeric AS bs,
        CASE WHEN version()~'mingw32' OR version()~'64-bit|x86_64|ppc64|ia64|amd64' THEN 8 ELSE 4 END AS ma,
        24 AS page_hdr,
            23 + CASE WHEN MAX(coalesce(ps.null_frac,0)) > 0 THEN ( 7 + count(ps.attname) ) / 8 ELSE 0::int END
            + CASE WHEN bool_or(att.attname = 'oid' and att.attnum < 0) THEN 4 ELSE 0 END AS tpl_hdr_size,
        sum( (1-coalesce(ps.null_frac, 0)) * coalesce(ps.avg_width, 0) ) AS tpl_data_size
    FROM pg_attribute AS att
             JOIN pg_class AS pc ON att.attrelid = pc.oid
             JOIN pg_namespace AS pn ON pn.oid = pc.relnamespace
             LEFT JOIN pg_stats AS ps ON ps.schemaname=pn.nspname
        AND ps.tablename = pc.relname AND ps.inherited=false AND ps.attname=att.attname
             LEFT JOIN pg_class AS toast ON pc.reltoastrelid = toast.oid
    WHERE NOT att.attisdropped
      AND pc.relkind = 'r'
    and pn.nspname = 'public'::text
    GROUP BY tblid, tblname,pc.reltuples,heappages,toastpages,toasttuples,fillfactor,bs,page_hdr
    ORDER BY tblname
),
     tables_pages_size as (
         SELECT
             ( 4 + tpl_hdr_size + tpl_data_size + (2*ma)
                 - CASE WHEN tpl_hdr_size%ma = 0 THEN ma ELSE tpl_hdr_size%ma END
                 - CASE WHEN ceil(tpl_data_size)::int%ma = 0 THEN ma ELSE ceil(tpl_data_size)::int%ma END
                 ) AS tpl_size, bs - page_hdr AS size_per_block, (heappages + toastpages) AS tblpages, heappages,
             toastpages, reltuples, toasttuples, bs, page_hdr, tblid, tblname, fillfactor
         FROM tables_stats as s
     ),
     relation_stats as (
         SELECT
                 ceil( reltuples / ( (bs-page_hdr)*fillfactor/(tpl_size*100) ) ) + ceil( toasttuples / 4 ) AS est_tblpages_ff,
             tblpages, fillfactor, bs, tblid, tblname, heappages, toastpages
         FROM tables_pages_size AS s2
     )
SELECT tblname,
       pg_table_size(tblid) as table_size,
    fillfactor,
    CASE WHEN tblpages - est_tblpages_ff > 0
             THEN (tblpages-est_tblpages_ff)*bs
         ELSE 0
        END AS bloat_size,
    CASE WHEN tblpages - est_tblpages_ff > 0
             THEN 100 * (tblpages - est_tblpages_ff)/tblpages::float
         ELSE 0
        END AS bloat_ratio
FROM relation_stats AS s3
ORDER BY tblname;
