## User-defined (custom) checks

You can add your own database structure checks.
To do this, you need to create a class that inherits from the [AbstractCheckOnHost](../pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/checks/host/AbstractCheckOnHost.java) class:

```java
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.core.checks.common.StandardCheckInfo;
import io.github.mfvanek.pg.core.checks.extractors.ColumnWithTypeExtractor;
import io.github.mfvanek.pg.core.checks.host.AbstractCheckOnHost;
import io.github.mfvanek.pg.core.utils.NamedParametersParser;
import io.github.mfvanek.pg.model.column.ColumnWithType;
import io.github.mfvanek.pg.model.context.PgContext;
import org.jspecify.annotations.NullMarked;

import java.util.List;

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
                order by table_name, column_name;""")));
    }

    @Override
    protected List<ColumnWithType> doCheck(final PgContext pgContext) {
        return executeQuery(pgContext, ColumnWithTypeExtractor.of());
    }
}
```

- Come up with a unique name for the new check.
- Write the appropriate JDBC-compatible SQL query.
  In the example above, we use the `NamedParametersParser.parse()` method to replace the named parameter `:schema_name_param` with a `?` placeholder.
- Convert the query results to a suitable instance of the domain model. To do this, you can use one of the [standard extractors](../pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/checks/extractors).

More examples you can find in the [pg-index-health-demo](https://github.com/mfvanek/pg-index-health-demo) project.
