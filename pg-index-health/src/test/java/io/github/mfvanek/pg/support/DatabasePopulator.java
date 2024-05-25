/*
 * Copyright (c) 2019-2024. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support;

import io.github.mfvanek.pg.model.validation.Validators;
import io.github.mfvanek.pg.support.statements.AddBlankCommentOnColumnsStatement;
import io.github.mfvanek.pg.support.statements.AddBlankCommentOnFunctionsStatement;
import io.github.mfvanek.pg.support.statements.AddBlankCommentOnTablesStatement;
import io.github.mfvanek.pg.support.statements.AddCommentOnColumnsStatement;
import io.github.mfvanek.pg.support.statements.AddCommentOnFunctionsStatement;
import io.github.mfvanek.pg.support.statements.AddCommentOnProceduresStatement;
import io.github.mfvanek.pg.support.statements.AddCommentOnTablesStatement;
import io.github.mfvanek.pg.support.statements.AddInvalidForeignKeyStatement;
import io.github.mfvanek.pg.support.statements.AddLinksBetweenAccountsAndClientsStatement;
import io.github.mfvanek.pg.support.statements.ConvertColumnToJsonTypeStatement;
import io.github.mfvanek.pg.support.statements.CreateAccountsTableStatement;
import io.github.mfvanek.pg.support.statements.CreateClientsTableStatement;
import io.github.mfvanek.pg.support.statements.CreateCustomCollationStatement;
import io.github.mfvanek.pg.support.statements.CreateDuplicatedCustomCollationIndexStatement;
import io.github.mfvanek.pg.support.statements.CreateDuplicatedHashIndexStatement;
import io.github.mfvanek.pg.support.statements.CreateDuplicatedIndexStatement;
import io.github.mfvanek.pg.support.statements.CreateForeignKeyOnNullableColumnStatement;
import io.github.mfvanek.pg.support.statements.CreateFunctionsStatement;
import io.github.mfvanek.pg.support.statements.CreateIndexWithBooleanValues;
import io.github.mfvanek.pg.support.statements.CreateIndexWithNullValues;
import io.github.mfvanek.pg.support.statements.CreateIndexesOnArrayColumn;
import io.github.mfvanek.pg.support.statements.CreateIndexesWithDifferentOpclassStatement;
import io.github.mfvanek.pg.support.statements.CreateMaterializedViewStatement;
import io.github.mfvanek.pg.support.statements.CreateNotSuitableIndexForForeignKeyStatement;
import io.github.mfvanek.pg.support.statements.CreateProceduresStatement;
import io.github.mfvanek.pg.support.statements.CreateSchemaStatement;
import io.github.mfvanek.pg.support.statements.CreateSequenceStatement;
import io.github.mfvanek.pg.support.statements.CreateSuitableIndexForForeignKeyStatement;
import io.github.mfvanek.pg.support.statements.CreateTableWithCheckConstraintOnSerialPrimaryKey;
import io.github.mfvanek.pg.support.statements.CreateTableWithColumnOfBigSerialTypeStatement;
import io.github.mfvanek.pg.support.statements.CreateTableWithSerialPrimaryKeyReferencesToAnotherTable;
import io.github.mfvanek.pg.support.statements.CreateTableWithUniqueSerialColumn;
import io.github.mfvanek.pg.support.statements.CreateTableWithoutPrimaryKeyStatement;
import io.github.mfvanek.pg.support.statements.DbStatement;
import io.github.mfvanek.pg.support.statements.DropColumnStatement;
import io.github.mfvanek.pg.support.statements.InsertDataIntoTablesAction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity", "PMD.ExcessiveImports"})
public final class DatabasePopulator implements AutoCloseable {

    private final DataSource dataSource;
    private final String schemaName;
    private final Map<Integer, Runnable> actionsToExecuteOutsideTransaction = new TreeMap<>();
    private final Map<Integer, DbStatement> statementsToExecuteInSameTransaction = new TreeMap<>();
    private final boolean supportsProcedures;

    private DatabasePopulator(@Nonnull final DataSource dataSource, @Nonnull final String schemaName, final boolean supportsProcedures) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.schemaName = Validators.notBlank(schemaName, "schemaName");
        this.supportsProcedures = supportsProcedures;
        this.statementsToExecuteInSameTransaction.putIfAbsent(1, new CreateSchemaStatement(schemaName));
        this.statementsToExecuteInSameTransaction.putIfAbsent(2, new CreateClientsTableStatement(schemaName));
        this.statementsToExecuteInSameTransaction.putIfAbsent(3, new CreateAccountsTableStatement(schemaName));
    }

    static DatabasePopulator builder(@Nonnull final DataSource dataSource, @Nonnull final String schemaName, final boolean supportsProcedures) {
        return new DatabasePopulator(dataSource, schemaName, supportsProcedures);
    }

    @Nonnull
    public DatabasePopulator withCustomCollation() {
        statementsToExecuteInSameTransaction.putIfAbsent(4, new CreateCustomCollationStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withReferences() {
        statementsToExecuteInSameTransaction.putIfAbsent(5, new AddLinksBetweenAccountsAndClientsStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withData() {
        actionsToExecuteOutsideTransaction.putIfAbsent(20, new InsertDataIntoTablesAction(dataSource, schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withInvalidIndex() {
        actionsToExecuteOutsideTransaction.putIfAbsent(30, this::createInvalidIndex);
        return this;
    }

    @Nonnull
    public DatabasePopulator withDuplicatedIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(40, new CreateDuplicatedIndexStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withDuplicatedHashIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(41, new CreateDuplicatedHashIndexStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withNonSuitableIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(43, new CreateNotSuitableIndexForForeignKeyStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withSuitableIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(44, new CreateSuitableIndexForForeignKeyStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withTableWithoutPrimaryKey() {
        statementsToExecuteInSameTransaction.putIfAbsent(47, new CreateTableWithoutPrimaryKeyStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withNullValuesInIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(48, new CreateIndexWithNullValues(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withBooleanValuesInIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(49, new CreateIndexWithBooleanValues(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withDifferentOpclassIndexes() {
        statementsToExecuteInSameTransaction.putIfAbsent(50, new CreateIndexesWithDifferentOpclassStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withMaterializedView() {
        statementsToExecuteInSameTransaction.putIfAbsent(55, new CreateMaterializedViewStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withDuplicatedCustomCollationIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(58, new CreateDuplicatedCustomCollationIndexStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withForeignKeyOnNullableColumn() {
        statementsToExecuteInSameTransaction.putIfAbsent(60, new CreateForeignKeyOnNullableColumnStatement(schemaName));
        return withTableWithoutPrimaryKey();
    }

    @Nonnull
    public DatabasePopulator withCommentOnTables() {
        statementsToExecuteInSameTransaction.putIfAbsent(64, new AddCommentOnTablesStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withBlankCommentOnTables() {
        statementsToExecuteInSameTransaction.putIfAbsent(65, new AddBlankCommentOnTablesStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withCommentOnColumns() {
        statementsToExecuteInSameTransaction.putIfAbsent(66, new AddCommentOnColumnsStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withBlankCommentOnColumns() {
        statementsToExecuteInSameTransaction.putIfAbsent(67, new AddBlankCommentOnColumnsStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withJsonType() {
        statementsToExecuteInSameTransaction.putIfAbsent(25, new ConvertColumnToJsonTypeStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withDroppedInfoColumn() {
        statementsToExecuteInSameTransaction.putIfAbsent(70, new DropColumnStatement(schemaName, "clients", "info"));
        return this;
    }

    @Nonnull
    public DatabasePopulator withSerialType() {
        statementsToExecuteInSameTransaction.putIfAbsent(75, new CreateTableWithColumnOfBigSerialTypeStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withDroppedSerialColumn() {
        statementsToExecuteInSameTransaction.putIfAbsent(76, new DropColumnStatement(schemaName, "bad_accounts", "real_account_id"));
        return this;
    }

    @Nonnull
    public DatabasePopulator withCheckConstraintOnSerialPrimaryKey() {
        statementsToExecuteInSameTransaction.putIfAbsent(80, new CreateTableWithCheckConstraintOnSerialPrimaryKey(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withUniqueConstraintOnSerialColumn() {
        statementsToExecuteInSameTransaction.putIfAbsent(81, new CreateTableWithUniqueSerialColumn(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withSerialPrimaryKeyReferencesToAnotherTable() {
        statementsToExecuteInSameTransaction.putIfAbsent(82, new CreateTableWithSerialPrimaryKeyReferencesToAnotherTable(schemaName));
        return withCheckConstraintOnSerialPrimaryKey()
            .withUniqueConstraintOnSerialColumn();
    }

    @Nonnull
    public DatabasePopulator withFunctions() {
        statementsToExecuteInSameTransaction.putIfAbsent(90, new CreateFunctionsStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withProcedures() {
        if (supportsProcedures) {
            statementsToExecuteInSameTransaction.putIfAbsent(91, new CreateProceduresStatement(schemaName));
        }
        return this;
    }

    @Nonnull
    public DatabasePopulator withBlankCommentOnFunctions() {
        statementsToExecuteInSameTransaction.putIfAbsent(92, new AddBlankCommentOnFunctionsStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withCommentOnFunctions() {
        statementsToExecuteInSameTransaction.putIfAbsent(93, new AddCommentOnFunctionsStatement(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withCommentOnProcedures() {
        if (supportsProcedures) {
            statementsToExecuteInSameTransaction.putIfAbsent(94, new AddCommentOnProceduresStatement(schemaName));
        }
        return this;
    }

    @Nonnull
    public DatabasePopulator withNotValidConstraints() {
        statementsToExecuteInSameTransaction.putIfAbsent(95, new AddInvalidForeignKeyStatement(schemaName));
        return this;
    }

    public DatabasePopulator withBtreeIndexesOnArrayColumn() {
        statementsToExecuteInSameTransaction.putIfAbsent(96, new CreateIndexesOnArrayColumn(schemaName));
        return this;
    }

    @Nonnull
    public DatabasePopulator withSequenceOverflow() {
        statementsToExecuteInSameTransaction.putIfAbsent(97, new CreateSequenceStatement(schemaName));
        return this;
    }

    public void populate() {
        ExecuteUtils.executeInTransaction(dataSource, statementsToExecuteInSameTransaction.values());
        actionsToExecuteOutsideTransaction.forEach((k, v) -> v.run());
    }

    private void createInvalidIndex() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(String.format("create unique index concurrently if not exists " +
                "i_clients_last_name_first_name on %s.clients (last_name, first_name)", schemaName));
        } catch (SQLException ignored) {
            // do nothing, just skip error
        }
    }

    @Override
    public void close() {
        ExecuteUtils.executeOnDatabase(dataSource, statement -> statement.execute(String.format("drop schema if exists %s cascade", schemaName)));
    }
}
