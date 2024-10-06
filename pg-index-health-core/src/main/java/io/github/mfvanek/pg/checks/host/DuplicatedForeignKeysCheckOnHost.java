package io.github.mfvanek.pg.checks.host;

import io.github.mfvanek.pg.checks.extractors.ForeignKeyExtractor;
import io.github.mfvanek.pg.common.maintenance.Diagnostic;
import io.github.mfvanek.pg.common.maintenance.ResultSetExtractor;
import io.github.mfvanek.pg.connection.PgConnection;
import io.github.mfvanek.pg.model.PgContext;
import io.github.mfvanek.pg.model.constraint.DuplicatedForeignKeys;
import io.github.mfvanek.pg.model.constraint.ForeignKey;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Check for duplicated (completely identical) foreign keys on a specific host.
 *
 * @author Ivan Vahrushev
 * @since 0.13.1
 */
public class DuplicatedForeignKeysCheckOnHost extends AbstractCheckOnHost<DuplicatedForeignKeys> {

    private final ResultSetExtractor<ForeignKey> defaultExtractor = ForeignKeyExtractor.ofDefault();
    private final ResultSetExtractor<ForeignKey> duplicateKeyExtractor = ForeignKeyExtractor.withPrefix("duplicate");

    /**
     * Creates a new {@code DuplicatedForeignKeysCheckOnHost} object
     *
     * @param pgConnection connection to the PostgreSQL database, must not be null
     */
    public DuplicatedForeignKeysCheckOnHost(@Nonnull final PgConnection pgConnection) {
        super(DuplicatedForeignKeys.class, pgConnection, Diagnostic.DUPLICATED_FOREIGN_KEYS);
    }

    /**
     * Returns duplicated (completely identical) foreign keys in the specified schema.
     *
     * @param pgContext check's context with the specified schema
     * @return list of duplicated foreign keys
     */
    @Nonnull
    @Override
    public List<DuplicatedForeignKeys> check(@Nonnull final PgContext pgContext) {
        return executeQuery(pgContext, rs -> {
            final ForeignKey first = defaultExtractor.extractData(rs);
            final ForeignKey second = duplicateKeyExtractor.extractData(rs);
            return DuplicatedForeignKeys.of(first, second);
        });
    }
}
