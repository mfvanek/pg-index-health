/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
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
import io.github.mfvanek.pg.core.fixtures.support.statements.AddMoneyColumnStatement;
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
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateIndexWithUnnecessaryWhereClauseStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateIndexesOnArrayColumnStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateIndexesWithDifferentOpclassStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateMaterializedViewStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateNotSuitableIndexForForeignKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedIndexWithUnnecessaryWhereClauseStatement;
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
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWhereAllColumnsNullableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithCheckConstraintOnSerialPrimaryKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithColumnOfBigSerialTypeStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithFixedLengthVarcharStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithIdentityPrimaryKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithNaturalKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithSerialPrimaryKeyReferencesToAnotherTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithTimestampInTheMiddleStatement;
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
import javax.sql.DataSource;

@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity", "PMD.ExcessivePublicCount"})
public final class DatabasePopulator implements AutoCloseable {

    private final DataSource dataSource;
    private final String schemaName;
    private final Map<Integer, Runnable> actionsToExecuteOutsideTransaction = new TreeMap<>();
    private final Map<Integer, DbStatement> statementsToExecuteInSameTransaction = new TreeMap<>();
    private final boolean supportsProcedures;

    private DatabasePopulator(final DataSource dataSource, final String schemaName, final boolean supportsProcedures) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.schemaName = Validators.notBlank(schemaName, "schemaName");
        this.supportsProcedures = supportsProcedures;
        register(1, new CreateSchemaStatement());
        register(2, new CreateClientsTableStatement());
        register(3, new CreateAccountsTableStatement());
    }

    static DatabasePopulator builder(final DataSource dataSource, final String schemaName, final boolean supportsProcedures) {
        return new DatabasePopulator(dataSource, schemaName, supportsProcedures);
    }

    public DatabasePopulator withCustomCollation() {
        return register(4, new CreateCustomCollationStatement());
    }

    public DatabasePopulator withReferences() {
        return register(5, new AddLinksBetweenAccountsAndClientsStatement());
    }

    public DatabasePopulator withData() {
        actionsToExecuteOutsideTransaction.putIfAbsent(20, new InsertDataIntoTablesAction(dataSource, schemaName));
        return this;
    }

    public DatabasePopulator withInvalidIndex() {
        actionsToExecuteOutsideTransaction.putIfAbsent(30, this::createInvalidIndex);
        return this;
    }

    public DatabasePopulator withDuplicatedIndex() {
        return register(40, new CreateDuplicatedIndexStatement());
    }

    public DatabasePopulator withDuplicatedHashIndex() {
        return register(41, new CreateDuplicatedHashIndexStatement());
    }

    public DatabasePopulator withNonSuitableIndex() {
        return register(43, new CreateNotSuitableIndexForForeignKeyStatement());
    }

    public DatabasePopulator withSuitableIndex() {
        return register(44, new CreateSuitableIndexForForeignKeyStatement());
    }

    public DatabasePopulator withTableWithoutPrimaryKey() {
        return register(47, new CreateTableWithoutPrimaryKeyStatement());
    }

    public DatabasePopulator withNullValuesInIndex() {
        return register(48, new CreateIndexWithNullValuesStatement());
    }

    public DatabasePopulator withBooleanValuesInIndex() {
        return register(49, new CreateIndexWithBooleanValuesStatement());
    }

    public DatabasePopulator withDifferentOpclassIndexes() {
        return register(50, new CreateIndexesWithDifferentOpclassStatement());
    }

    public DatabasePopulator withMaterializedView() {
        return register(55, new CreateMaterializedViewStatement());
    }

    public DatabasePopulator withDuplicatedCustomCollationIndex() {
        return register(58, new CreateDuplicatedCustomCollationIndexStatement());
    }

    public DatabasePopulator withForeignKeyOnNullableColumn() {
        return register(60, new CreateForeignKeyOnNullableColumnStatement())
            .withTableWithoutPrimaryKey();
    }

    public DatabasePopulator withCommentOnTables() {
        return register(64, new AddCommentOnTablesStatement());
    }

    public DatabasePopulator withBlankCommentOnTables() {
        return register(65, new AddBlankCommentOnTablesStatement());
    }

    public DatabasePopulator withCommentOnColumns() {
        return register(66, new AddCommentOnColumnsStatement());
    }

    public DatabasePopulator withBlankCommentOnColumns() {
        return register(67, new AddBlankCommentOnColumnsStatement());
    }

    public DatabasePopulator withJsonType() {
        return register(25, new ConvertColumnToJsonTypeStatement());
    }

    public DatabasePopulator withDroppedInfoColumn() {
        return register(70, new DropColumnStatement("clients", "info"));
    }

    public DatabasePopulator withSerialType() {
        return register(75, new CreateTableWithColumnOfBigSerialTypeStatement());
    }

    public DatabasePopulator withDroppedSerialColumn() {
        return register(76, new DropColumnStatement("bad_accounts", "real_account_id"));
    }

    public DatabasePopulator withCheckConstraintOnSerialPrimaryKey() {
        return register(80, new CreateTableWithCheckConstraintOnSerialPrimaryKeyStatement());
    }

    public DatabasePopulator withUniqueConstraintOnSerialColumn() {
        return register(81, new CreateTableWithUniqueSerialColumnStatement());
    }

    public DatabasePopulator withSerialPrimaryKeyReferencesToAnotherTable() {
        return register(82, new CreateTableWithSerialPrimaryKeyReferencesToAnotherTableStatement())
            .withCheckConstraintOnSerialPrimaryKey()
            .withUniqueConstraintOnSerialColumn();
    }

    public DatabasePopulator withFunctions() {
        return register(90, new CreateFunctionsStatement());
    }

    public DatabasePopulator withProcedures() {
        if (supportsProcedures) {
            return register(91, new CreateProceduresStatement());
        }
        return this;
    }

    public DatabasePopulator withBlankCommentOnFunctions() {
        return register(92, new AddBlankCommentOnFunctionsStatement());
    }

    public DatabasePopulator withCommentOnFunctions() {
        return register(93, new AddCommentOnFunctionsStatement());
    }

    public DatabasePopulator withCommentOnProcedures() {
        if (supportsProcedures) {
            return register(94, new AddCommentOnProceduresStatement());
        }
        return this;
    }

    public DatabasePopulator withNotValidConstraints() {
        return register(95, new AddInvalidForeignKeyStatement());
    }

    public DatabasePopulator withBtreeIndexesOnArrayColumn() {
        return register(96, new CreateIndexesOnArrayColumnStatement());
    }

    public DatabasePopulator withSequenceOverflow() {
        return register(97, new CreateSequenceStatement());
    }

    public DatabasePopulator withIdentityPrimaryKey() {
        return register(98, new CreateTableWithIdentityPrimaryKeyStatement());
    }

    public DatabasePopulator withDuplicatedForeignKeys() {
        return register(105, new AddDuplicatedForeignKeysStatement());
    }

    public DatabasePopulator withIntersectedForeignKeys() {
        return register(106, new AddIntersectedForeignKeysStatement());
    }

    public DatabasePopulator withPartitionedTableWithoutComments() {
        return register(110, new CreatePartitionedTableWithoutCommentsStatement());
    }

    public DatabasePopulator withPartitionedTableWithoutPrimaryKey() {
        return register(111, new CreatePartitionedTableWithoutPrimaryKeyStatement());
    }

    public DatabasePopulator withPrimaryKeyForDefaultPartition() {
        return register(112, new AddPrimaryKeyForDefaultPartitionStatement());
    }

    public DatabasePopulator withNullableIndexesInPartitionedTable() {
        return register(113, new CreatePartitionedTableWithNullableFieldsStatement());
    }

    public DatabasePopulator withVeryLongNamesInPartitionedTable() {
        return register(114, new CreatePartitionedTableWithVeryLongNamesStatement());
    }

    public DatabasePopulator withJsonAndSerialColumnsInPartitionedTable() {
        return register(115, new CreatePartitionedTableWithJsonAndSerialColumnsStatement());
    }

    public DatabasePopulator withSerialAndForeignKeysInPartitionedTable() {
        return register(116, new CreatePartitionedTableWithSerialAndForeignKeysStatement());
    }

    public DatabasePopulator withDuplicatedAndIntersectedIndexesInPartitionedTable() {
        return register(117, new CreateDuplicatedAndIntersectedIndexesInPartitionedTableStatement());
    }

    public DatabasePopulator withNotValidConstraintInPartitionedTable() {
        return register(118, new AddNotValidConstraintToPartitionedTableStatement());
    }

    public DatabasePopulator withBtreeIndexOnArrayColumnInPartitionedTable() {
        return register(119, new AddArrayColumnAndIndexToPartitionedTableStatement());
    }

    public DatabasePopulator withDuplicatedForeignKeysInPartitionedTable() {
        return register(120, new AddDuplicatedForeignKeysToPartitionedTableStatement());
    }

    public DatabasePopulator withIntersectedForeignKeysInPartitionedTable() {
        return register(121, new AddIntersectedForeignKeysToPartitionedTableStatement());
    }

    public DatabasePopulator withDroppedColumnInPartitionedTable() {
        return register(122, new CreatePartitionedTableWithDroppedColumnStatement());
    }

    public DatabasePopulator withBadlyNamedPartitionedTable() {
        return register(123, new CreateBadlyNamedPartitionedTableStatement());
    }

    public DatabasePopulator withVarcharInPartitionedTable() {
        return register(124, new CreatePartitionedTableWithVarcharStatement());
    }

    public DatabasePopulator withUnnecessaryWhereClauseInPartitionedIndex() {
        return register(125, new CreatePartitionedIndexWithUnnecessaryWhereClauseStatement());
    }

    public DatabasePopulator withEmptyTable() {
        return register(130, new CreateEmptyTableStatement());
    }

    public DatabasePopulator withBadlyNamedObjects() {
        return register(135, new CreateBadlyNamedObjectsStatement());
    }

    public DatabasePopulator withVarcharInsteadOfUuid() {
        return register(136, new CreateTableWithFixedLengthVarcharStatement());
    }

    public DatabasePopulator withDroppedAccountNumberColumn() {
        return register(137, new DropColumnStatement("accounts", "account_number"));
    }

    public DatabasePopulator withUnnecessaryWhereClause() {
        return register(138, new CreateIndexWithUnnecessaryWhereClauseStatement());
    }

    public DatabasePopulator withNaturalKeys() {
        return register(139, new CreateTableWithNaturalKeyStatement());
    }

    public DatabasePopulator withMoneyColumn() {
        return register(140, new AddMoneyColumnStatement());
    }

    public DatabasePopulator withTimestampInTheMiddle() {
        return register(141, new CreateTableWithTimestampInTheMiddleStatement());
    }

    public DatabasePopulator withTableWhereAllColumnsNullable() {
        return register(144, new CreateTableWhereAllColumnsNullableStatement());
    }

    public void populate() {
        try (SchemaNameHolder ignored = SchemaNameHolder.with(schemaName)) {
            ExecuteUtils.executeInTransaction(dataSource, statementsToExecuteInSameTransaction.values());
        }
        actionsToExecuteOutsideTransaction.forEach((k, v) -> v.run());
    }

    private DatabasePopulator register(final int statementOrder, final DbStatement dbStatement) {
        statementsToExecuteInSameTransaction.putIfAbsent(statementOrder, dbStatement);
        return this;
    }

    private void createInvalidIndex() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(
                String.format(
                    Locale.ROOT,
                    "create unique index concurrently if not exists i_clients_last_name_first_name on %s.clients (last_name, first_name)",
                    schemaName)
            );
        } catch (SQLException ignored) {
            // do nothing, just skip error
        }
    }

    @Override
    public void close() {
        ExecuteUtils.executeOnDatabase(dataSource, statement -> statement.execute(String.format(Locale.ROOT, "drop schema if exists %s cascade", schemaName)));
    }
}
