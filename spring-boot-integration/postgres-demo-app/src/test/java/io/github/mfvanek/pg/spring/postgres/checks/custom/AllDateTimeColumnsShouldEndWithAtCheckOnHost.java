/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring.postgres.checks.custom;

import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.StandardCheckInfo;
import io.github.mfvanek.pg.core.checks.extractors.ColumnWithTypeExtractor;
import io.github.mfvanek.pg.core.checks.host.AbstractCheckOnHost;
import io.github.mfvanek.pg.core.utils.NamedParametersParser;
import io.github.mfvanek.pg.model.column.ColumnWithType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class AllDateTimeColumnsShouldEndWithAtCheckOnHost extends AbstractCheckOnHost<ColumnWithType> {

    public AllDateTimeColumnsShouldEndWithAtCheckOnHost(final PgConnection pgConnection) {
        super(ColumnWithType.class, pgConnection,
            StandardCheckInfo.ofStatic("ALL_DATETIME_COLUMNS_SHOULD_END_WITH_AT", NamedParametersParser.parse("""
                select
                    t.oid::regclass::text as table_name,
                    col.attnotnull as column_not_null,
                    col.atttypid::regtype::text as column_type,
                    quote_ident(col.attname) as column_name
                from
                    pg_catalog.pg_class t
                    inner join pg_catalog.pg_namespace nsp on nsp.oid = t.relnamespace
                    inner join pg_catalog.pg_attribute col on col.attrelid = t.oid
                where
                    t.relkind in ('r', 'p') and
                    not t.relispartition and
                    col.attnum > 0 and /* to filter out system columns */
                    not col.attisdropped and
                    col.atttypid in ('timestamp without time zone'::regtype, 'timestamp with time zone'::regtype) and
                    right(col.attname, length('_at')) != '_at' and /* should end with _at */
                    nsp.nspname = :schema_name_param::text
                order by table_name, column_name;""")),
            ColumnWithTypeExtractor.of());
    }
}
