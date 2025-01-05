/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support;

import io.github.mfvanek.pg.core.fixtures.support.statements.AddBlankCommentOnColumnsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddBlankCommentOnFunctionsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddBlankCommentOnTablesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddCommentOnColumnsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddCommentOnFunctionsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddCommentOnProceduresStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddCommentOnTablesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddDuplicatedForeignKeysStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddIntersectedForeignKeysStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddInvalidForeignKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddLinksBetweenAccountsAndClientsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddPrimaryKeyForDefaultPartitionStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.ConvertColumnToJsonTypeStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateAccountsTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateClientsTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateCustomCollationStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateDuplicatedCustomCollationIndexStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateDuplicatedHashIndexStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateDuplicatedIndexStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateForeignKeyOnNullableColumnStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateFunctionsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateIndexWithBooleanValuesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateIndexWithNullValuesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateIndexesOnArrayColumnStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateIndexesWithDifferentOpclassStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateMaterializedViewStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateNotSuitableIndexForForeignKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithNullableFieldsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithVeryLongNamesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithoutCommentsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithoutPrimaryKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateProceduresStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateSchemaStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateSequenceStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateSuitableIndexForForeignKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithCheckConstraintOnSerialPrimaryKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithColumnOfBigSerialTypeStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithIdentityPrimaryKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithSerialPrimaryKeyReferencesToAnotherTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithUniqueSerialColumnStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithoutPrimaryKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.DbStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.DropColumnStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.InsertDataIntoTablesAction;
import io.github.mfvanek.pg.model.validation.Validators;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
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
        this.statementsToExecuteInSameTransaction.putIfAbsent(1, new CreateSchemaStatement());
        this.statementsToExecuteInSameTransaction.putIfAbsent(2, new CreateClientsTableStatement());
        this.statementsToExecuteInSameTransaction.putIfAbsent(3, new CreateAccountsTableStatement());
    }

    static DatabasePopulator builder(@Nonnull final DataSource dataSource, @Nonnull final String schemaName, final boolean supportsProcedures) {
        return new DatabasePopulator(dataSource, schemaName, supportsProcedures);
    }

    @Nonnull
    public DatabasePopulator withCustomCollation() {
        statementsToExecuteInSameTransaction.putIfAbsent(4, new CreateCustomCollationStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withReferences() {
        statementsToExecuteInSameTransaction.putIfAbsent(5, new AddLinksBetweenAccountsAndClientsStatement());
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
        statementsToExecuteInSameTransaction.putIfAbsent(40, new CreateDuplicatedIndexStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withDuplicatedHashIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(41, new CreateDuplicatedHashIndexStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withNonSuitableIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(43, new CreateNotSuitableIndexForForeignKeyStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withSuitableIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(44, new CreateSuitableIndexForForeignKeyStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withTableWithoutPrimaryKey() {
        statementsToExecuteInSameTransaction.putIfAbsent(47, new CreateTableWithoutPrimaryKeyStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withNullValuesInIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(48, new CreateIndexWithNullValuesStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withBooleanValuesInIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(49, new CreateIndexWithBooleanValuesStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withDifferentOpclassIndexes() {
        statementsToExecuteInSameTransaction.putIfAbsent(50, new CreateIndexesWithDifferentOpclassStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withMaterializedView() {
        statementsToExecuteInSameTransaction.putIfAbsent(55, new CreateMaterializedViewStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withDuplicatedCustomCollationIndex() {
        statementsToExecuteInSameTransaction.putIfAbsent(58, new CreateDuplicatedCustomCollationIndexStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withForeignKeyOnNullableColumn() {
        statementsToExecuteInSameTransaction.putIfAbsent(60, new CreateForeignKeyOnNullableColumnStatement());
        return withTableWithoutPrimaryKey();
    }

    @Nonnull
    public DatabasePopulator withCommentOnTables() {
        statementsToExecuteInSameTransaction.putIfAbsent(64, new AddCommentOnTablesStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withBlankCommentOnTables() {
        statementsToExecuteInSameTransaction.putIfAbsent(65, new AddBlankCommentOnTablesStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withCommentOnColumns() {
        statementsToExecuteInSameTransaction.putIfAbsent(66, new AddCommentOnColumnsStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withBlankCommentOnColumns() {
        statementsToExecuteInSameTransaction.putIfAbsent(67, new AddBlankCommentOnColumnsStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withJsonType() {
        statementsToExecuteInSameTransaction.putIfAbsent(25, new ConvertColumnToJsonTypeStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withDroppedInfoColumn() {
        statementsToExecuteInSameTransaction.putIfAbsent(70, new DropColumnStatement("clients", "info"));
        return this;
    }

    @Nonnull
    public DatabasePopulator withSerialType() {
        statementsToExecuteInSameTransaction.putIfAbsent(75, new CreateTableWithColumnOfBigSerialTypeStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withDroppedSerialColumn() {
        statementsToExecuteInSameTransaction.putIfAbsent(76, new DropColumnStatement("bad_accounts", "real_account_id"));
        return this;
    }

    @Nonnull
    public DatabasePopulator withCheckConstraintOnSerialPrimaryKey() {
        statementsToExecuteInSameTransaction.putIfAbsent(80, new CreateTableWithCheckConstraintOnSerialPrimaryKeyStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withUniqueConstraintOnSerialColumn() {
        statementsToExecuteInSameTransaction.putIfAbsent(81, new CreateTableWithUniqueSerialColumnStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withSerialPrimaryKeyReferencesToAnotherTable() {
        statementsToExecuteInSameTransaction.putIfAbsent(82, new CreateTableWithSerialPrimaryKeyReferencesToAnotherTableStatement());
        return withCheckConstraintOnSerialPrimaryKey()
            .withUniqueConstraintOnSerialColumn();
    }

    @Nonnull
    public DatabasePopulator withFunctions() {
        statementsToExecuteInSameTransaction.putIfAbsent(90, new CreateFunctionsStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withProcedures() {
        if (supportsProcedures) {
            statementsToExecuteInSameTransaction.putIfAbsent(91, new CreateProceduresStatement());
        }
        return this;
    }

    @Nonnull
    public DatabasePopulator withBlankCommentOnFunctions() {
        statementsToExecuteInSameTransaction.putIfAbsent(92, new AddBlankCommentOnFunctionsStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withCommentOnFunctions() {
        statementsToExecuteInSameTransaction.putIfAbsent(93, new AddCommentOnFunctionsStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withCommentOnProcedures() {
        if (supportsProcedures) {
            statementsToExecuteInSameTransaction.putIfAbsent(94, new AddCommentOnProceduresStatement());
        }
        return this;
    }

    @Nonnull
    public DatabasePopulator withNotValidConstraints() {
        statementsToExecuteInSameTransaction.putIfAbsent(95, new AddInvalidForeignKeyStatement());
        return this;
    }

    public DatabasePopulator withBtreeIndexesOnArrayColumn() {
        statementsToExecuteInSameTransaction.putIfAbsent(96, new CreateIndexesOnArrayColumnStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withSequenceOverflow() {
        statementsToExecuteInSameTransaction.putIfAbsent(97, new CreateSequenceStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withIdentityPrimaryKey() {
        statementsToExecuteInSameTransaction.putIfAbsent(98, new CreateTableWithIdentityPrimaryKeyStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withDuplicatedForeignKeys() {
        statementsToExecuteInSameTransaction.putIfAbsent(105, new AddDuplicatedForeignKeysStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withIntersectedForeignKeys() {
        statementsToExecuteInSameTransaction.putIfAbsent(106, new AddIntersectedForeignKeysStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withPartitionedTableWithoutComments() {
        statementsToExecuteInSameTransaction.putIfAbsent(110, new CreatePartitionedTableWithoutCommentsStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withPartitionedTableWithoutPrimaryKey() {
        statementsToExecuteInSameTransaction.putIfAbsent(111, new CreatePartitionedTableWithoutPrimaryKeyStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withPrimaryKeyForDefaultPartition() {
        statementsToExecuteInSameTransaction.putIfAbsent(112, new AddPrimaryKeyForDefaultPartitionStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withNullableIndexesInPartitionedTable() {
        statementsToExecuteInSameTransaction.putIfAbsent(113, new CreatePartitionedTableWithNullableFieldsStatement());
        return this;
    }

    @Nonnull
    public DatabasePopulator withVeryLongNamesInPartitionedTable() {
        statementsToExecuteInSameTransaction.putIfAbsent(114, new CreatePartitionedTableWithVeryLongNamesStatement());
        return this;
    }

    public void populate() {
        try (SchemaNameHolder ignored = SchemaNameHolder.with(schemaName)) {
            ExecuteUtils.executeInTransaction(dataSource, statementsToExecuteInSameTransaction.values());
        }
        actionsToExecuteOutsideTransaction.forEach((k, v) -> v.run());
    }

    private void createInvalidIndex() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(String.format(Locale.ROOT, "create unique index concurrently if not exists " +
                "i_clients_last_name_first_name on %s.clients (last_name, first_name)", schemaName));
        } catch (SQLException ignored) {
            // do nothing, just skip error
        }
    }

    @Override
    public void close() {
        ExecuteUtils.executeOnDatabase(dataSource, statement -> statement.execute(String.format(Locale.ROOT, "drop schema if exists %s cascade", schemaName)));
    }
}
