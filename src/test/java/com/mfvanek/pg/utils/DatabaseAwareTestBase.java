/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.utils;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class DatabaseAwareTestBase {

    private final DataSource dataSource;

    protected DatabaseAwareTestBase(@Nonnull final DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    @Nonnull
    protected DatabasePopulator createDatabasePopulator() {
        return new DatabasePopulator(dataSource);
    }

    @Nonnull
    protected DataSource getDataSource() {
        return dataSource;
    }

    protected void executeTestOnDatabase(@Nonnull final Consumer<DatabasePopulator> databasePopulatorConsumer,
                                         @Nonnull final TestExecutor testExecutor) {
        try (var databasePopulator = createDatabasePopulator()) {
            databasePopulatorConsumer.accept(databasePopulator);
            testExecutor.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
