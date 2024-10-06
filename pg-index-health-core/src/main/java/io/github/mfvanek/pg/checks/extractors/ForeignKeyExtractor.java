package io.github.mfvanek.pg.checks.extractors;

import io.github.mfvanek.pg.common.maintenance.ResultSetExtractor;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.constraint.ForeignKey;
import io.github.mfvanek.pg.utils.ColumnsInForeignKeyParser;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

import static io.github.mfvanek.pg.checks.extractors.TableExtractor.TABLE_NAME;

/**
 * A mapper from raw data to {@link ForeignKey} model.
 *
 * @author Ivan Vahrushev
 * @since 0.13.1
 */
public class ForeignKeyExtractor implements ResultSetExtractor<ForeignKey> {

    public static final String CONSTRAINT_NAME = "constraint_name";

    private final String prefix;

    private ForeignKeyExtractor(@Nonnull final String prefix) {
        this.prefix = Objects.requireNonNull(prefix, "prefix cannot be null");
    }

    /**
     * {@inheritDoc}
     */
    @Nonnull
    @Override
    public ForeignKey extractData(@Nonnull ResultSet resultSet) throws SQLException {
        final String tableName = resultSet.getString(TABLE_NAME);
        final String constraintName = resultSet.getString(decoratedColumnName(CONSTRAINT_NAME));
        final Array columnsArray = resultSet.getArray(decoratedColumnName("columns"));
        final String[] rawColumns = (String[]) columnsArray.getArray();
        final List<Column> columns = ColumnsInForeignKeyParser.parseRawColumnData(tableName, rawColumns);
        return ForeignKey.of(tableName, constraintName, columns);
    }

    private String decoratedColumnName(@Nonnull final String columnName) {
        if (prefix.isBlank()) {
            return columnName;
        }
        if (prefix.endsWith("_")) {
            return prefix + columnName;
        }
        return prefix + "_" + columnName;
    }

    /**
     * Creates default {@code ForeignKeyExtractor} instance.
     *
     * @return {@code ForeignKeyExtractor} instance
     */
    @Nonnull
    public static ResultSetExtractor<ForeignKey> ofDefault() {
        return new ForeignKeyExtractor("");
    }

    /**
     * Creates {@code ForeignKeyExtractor} instance with given prefix.
     *
     * @param prefix prefix for foreign key column; must be non-null.
     * @return {@code ForeignKeyExtractor} instance
     */
    @Nonnull
    public static ResultSetExtractor<ForeignKey> withPrefix(@Nonnull final String prefix) {
        return new ForeignKeyExtractor(prefix);
    }
}
