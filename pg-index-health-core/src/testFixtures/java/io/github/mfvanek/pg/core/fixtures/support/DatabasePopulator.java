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

import io.github.mfvanek.pg.core.fixtures.support.statements.AddArrayColumnAndIndexToPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddBlankCommentOnColumnsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddBlankCommentOnFunctionsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddBlankCommentOnTablesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddCommentOnColumnsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddCommentOnFunctionsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddCommentOnProceduresStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddCommentOnTablesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddDuplicatedForeignKeysStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddDuplicatedForeignKeysToPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddIntersectedForeignKeysStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddIntersectedForeignKeysToPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddInvalidForeignKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddLinksBetweenAccountsAndClientsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddNotValidConstraintToPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddPrimaryKeyForDefaultPartitionStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.ConvertColumnToJsonTypeStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateAccountsTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateBadlyNamedObjectsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateBadlyNamedPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateClientsTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateCustomCollationStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateDuplicatedAndIntersectedIndexesInPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateDuplicatedCustomCollationIndexStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateDuplicatedHashIndexStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateDuplicatedIndexStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateEmptyTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateForeignKeyOnNullableColumnStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateFunctionsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateIndexWithBooleanValuesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateIndexWithNullValuesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateIndexesOnArrayColumnStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateIndexesWithDifferentOpclassStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateMaterializedViewStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateNotSuitableIndexForForeignKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithDroppedColumnStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithJsonAndSerialColumnsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithNullableFieldsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithSerialAndForeignKeysStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithVarcharStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithVeryLongNamesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithoutCommentsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithoutPrimaryKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateProceduresStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateSchemaStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateSequenceStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateSuitableIndexForForeignKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithCheckConstraintOnSerialPrimaryKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithColumnOfBigSerialTypeStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithFixedLengthVarcharStatement;
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

@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity", "PMD.ExcessiveImports", "PMD.ExcessivePublicCount"})
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
        register(1, new CreateSchemaStatement());
        register(2, new CreateClientsTableStatement());
        register(3, new CreateAccountsTableStatement());
    }

    static DatabasePopulator builder(@Nonnull final DataSource dataSource, @Nonnull final String schemaName, final boolean supportsProcedures) {
        return new DatabasePopulator(dataSource, schemaName, supportsProcedures);
    }

    @Nonnull
    public DatabasePopulator withCustomCollation() {
        return register(4, new CreateCustomCollationStatement());
    }

    @Nonnull
    public DatabasePopulator withReferences() {
        return register(5, new AddLinksBetweenAccountsAndClientsStatement());
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
        return register(40, new CreateDuplicatedIndexStatement());
    }

    @Nonnull
    public DatabasePopulator withDuplicatedHashIndex() {
        return register(41, new CreateDuplicatedHashIndexStatement());
    }

    @Nonnull
    public DatabasePopulator withNonSuitableIndex() {
        return register(43, new CreateNotSuitableIndexForForeignKeyStatement());
    }

    @Nonnull
    public DatabasePopulator withSuitableIndex() {
        return register(44, new CreateSuitableIndexForForeignKeyStatement());
    }

    @Nonnull
    public DatabasePopulator withTableWithoutPrimaryKey() {
        return register(47, new CreateTableWithoutPrimaryKeyStatement());
    }

    @Nonnull
    public DatabasePopulator withNullValuesInIndex() {
        return register(48, new CreateIndexWithNullValuesStatement());
    }

    @Nonnull
    public DatabasePopulator withBooleanValuesInIndex() {
        return register(49, new CreateIndexWithBooleanValuesStatement());
    }

    @Nonnull
    public DatabasePopulator withDifferentOpclassIndexes() {
        return register(50, new CreateIndexesWithDifferentOpclassStatement());
    }

    @Nonnull
    public DatabasePopulator withMaterializedView() {
        return register(55, new CreateMaterializedViewStatement());
    }

    @Nonnull
    public DatabasePopulator withDuplicatedCustomCollationIndex() {
        return register(58, new CreateDuplicatedCustomCollationIndexStatement());
    }

    @Nonnull
    public DatabasePopulator withForeignKeyOnNullableColumn() {
        return register(60, new CreateForeignKeyOnNullableColumnStatement())
            .withTableWithoutPrimaryKey();
    }

    @Nonnull
    public DatabasePopulator withCommentOnTables() {
        return register(64, new AddCommentOnTablesStatement());
    }

    @Nonnull
    public DatabasePopulator withBlankCommentOnTables() {
        return register(65, new AddBlankCommentOnTablesStatement());
    }

    @Nonnull
    public DatabasePopulator withCommentOnColumns() {
        return register(66, new AddCommentOnColumnsStatement());
    }

    @Nonnull
    public DatabasePopulator withBlankCommentOnColumns() {
        return register(67, new AddBlankCommentOnColumnsStatement());
    }

    @Nonnull
    public DatabasePopulator withJsonType() {
        return register(25, new ConvertColumnToJsonTypeStatement());
    }

    @Nonnull
    public DatabasePopulator withDroppedInfoColumn() {
        return register(70, new DropColumnStatement("clients", "info"));
    }

    @Nonnull
    public DatabasePopulator withSerialType() {
        return register(75, new CreateTableWithColumnOfBigSerialTypeStatement());
    }

    @Nonnull
    public DatabasePopulator withDroppedSerialColumn() {
        return register(76, new DropColumnStatement("bad_accounts", "real_account_id"));
    }

    @Nonnull
    public DatabasePopulator withCheckConstraintOnSerialPrimaryKey() {
        return register(80, new CreateTableWithCheckConstraintOnSerialPrimaryKeyStatement());
    }

    @Nonnull
    public DatabasePopulator withUniqueConstraintOnSerialColumn() {
        return register(81, new CreateTableWithUniqueSerialColumnStatement());
    }

    @Nonnull
    public DatabasePopulator withSerialPrimaryKeyReferencesToAnotherTable() {
        return register(82, new CreateTableWithSerialPrimaryKeyReferencesToAnotherTableStatement())
            .withCheckConstraintOnSerialPrimaryKey()
            .withUniqueConstraintOnSerialColumn();
    }

    @Nonnull
    public DatabasePopulator withFunctions() {
        return register(90, new CreateFunctionsStatement());
    }

    @Nonnull
    public DatabasePopulator withProcedures() {
        if (supportsProcedures) {
            return register(91, new CreateProceduresStatement());
        }
        return this;
    }

    @Nonnull
    public DatabasePopulator withBlankCommentOnFunctions() {
        return register(92, new AddBlankCommentOnFunctionsStatement());
    }

    @Nonnull
    public DatabasePopulator withCommentOnFunctions() {
        return register(93, new AddCommentOnFunctionsStatement());
    }

    @Nonnull
    public DatabasePopulator withCommentOnProcedures() {
        if (supportsProcedures) {
            return register(94, new AddCommentOnProceduresStatement());
        }
        return this;
    }

    @Nonnull
    public DatabasePopulator withNotValidConstraints() {
        return register(95, new AddInvalidForeignKeyStatement());
    }

    public DatabasePopulator withBtreeIndexesOnArrayColumn() {
        return register(96, new CreateIndexesOnArrayColumnStatement());
    }

    @Nonnull
    public DatabasePopulator withSequenceOverflow() {
        return register(97, new CreateSequenceStatement());
    }

    @Nonnull
    public DatabasePopulator withIdentityPrimaryKey() {
        return register(98, new CreateTableWithIdentityPrimaryKeyStatement());
    }

    @Nonnull
    public DatabasePopulator withDuplicatedForeignKeys() {
        return register(105, new AddDuplicatedForeignKeysStatement());
    }

    @Nonnull
    public DatabasePopulator withIntersectedForeignKeys() {
        return register(106, new AddIntersectedForeignKeysStatement());
    }

    @Nonnull
    public DatabasePopulator withPartitionedTableWithoutComments() {
        return register(110, new CreatePartitionedTableWithoutCommentsStatement());
    }

    @Nonnull
    public DatabasePopulator withPartitionedTableWithoutPrimaryKey() {
        return register(111, new CreatePartitionedTableWithoutPrimaryKeyStatement());
    }

    @Nonnull
    public DatabasePopulator withPrimaryKeyForDefaultPartition() {
        return register(112, new AddPrimaryKeyForDefaultPartitionStatement());
    }

    @Nonnull
    public DatabasePopulator withNullableIndexesInPartitionedTable() {
        return register(113, new CreatePartitionedTableWithNullableFieldsStatement());
    }

    @Nonnull
    public DatabasePopulator withVeryLongNamesInPartitionedTable() {
        return register(114, new CreatePartitionedTableWithVeryLongNamesStatement());
    }

    @Nonnull
    public DatabasePopulator withJsonAndSerialColumnsInPartitionedTable() {
        return register(115, new CreatePartitionedTableWithJsonAndSerialColumnsStatement());
    }

    @Nonnull
    public DatabasePopulator withSerialAndForeignKeysInPartitionedTable() {
        return register(116, new CreatePartitionedTableWithSerialAndForeignKeysStatement());
    }

    @Nonnull
    public DatabasePopulator withDuplicatedAndIntersectedIndexesInPartitionedTable() {
        return register(117, new CreateDuplicatedAndIntersectedIndexesInPartitionedTableStatement());
    }

    @Nonnull
    public DatabasePopulator withNotValidConstraintInPartitionedTable() {
        return register(118, new AddNotValidConstraintToPartitionedTableStatement());
    }

    @Nonnull
    public DatabasePopulator withBtreeIndexOnArrayColumnInPartitionedTable() {
        return register(119, new AddArrayColumnAndIndexToPartitionedTableStatement());
    }

    @Nonnull
    public DatabasePopulator withDuplicatedForeignKeysInPartitionedTable() {
        return register(120, new AddDuplicatedForeignKeysToPartitionedTableStatement());
    }

    @Nonnull
    public DatabasePopulator withIntersectedForeignKeysInPartitionedTable() {
        return register(121, new AddIntersectedForeignKeysToPartitionedTableStatement());
    }

    @Nonnull
    public DatabasePopulator withDroppedColumnInPartitionedTable() {
        return register(122, new CreatePartitionedTableWithDroppedColumnStatement());
    }

    @Nonnull
    public DatabasePopulator withBadlyNamedPartitionedTable() {
        return register(123, new CreateBadlyNamedPartitionedTableStatement());
    }

    @Nonnull
    public DatabasePopulator withVarcharInPartitionedTable() {
        return register(124, new CreatePartitionedTableWithVarcharStatement());
    }

    @Nonnull
    public DatabasePopulator withEmptyTable() {
        return register(130, new CreateEmptyTableStatement());
    }

    @Nonnull
    public DatabasePopulator withBadlyNamedObjects() {
        return register(135, new CreateBadlyNamedObjectsStatement());
    }

    @Nonnull
    public DatabasePopulator withVarcharInsteadOfUuid() {
        return register(136, new CreateTableWithFixedLengthVarcharStatement());
    }

    public void populate() {
        try (SchemaNameHolder ignored = SchemaNameHolder.with(schemaName)) {
            ExecuteUtils.executeInTransaction(dataSource, statementsToExecuteInSameTransaction.values());
        }
        actionsToExecuteOutsideTransaction.forEach((k, v) -> v.run());
    }

    @Nonnull
    private DatabasePopulator register(final int statementOrder, @Nonnull final DbStatement dbStatement) {
        statementsToExecuteInSameTransaction.putIfAbsent(statementOrder, dbStatement);
        return this;
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
