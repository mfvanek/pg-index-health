/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
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
import io.github.mfvanek.pg.core.fixtures.support.statements.AddCompositeForeignKeyWithNullValuesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddCompositeForeignKeyWithNullValuesToPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddDuplicatedForeignKeysStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddDuplicatedForeignKeysToPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddIntersectedForeignKeysStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddIntersectedForeignKeysToPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddInvalidForeignKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddLinksBetweenAccountsAndClientsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddMoneyColumnStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddNotValidConstraintToPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddPrimaryKeyForDefaultPartitionStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddSelfReferencedForeignKeysStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.AddSelfReferencedForeignKeysToPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.ConvertColumnToJsonTypeStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateAccountsTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateBadlyNamedObjectsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateBadlyNamedPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateClientsTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateCustomCollationStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateDictTableStatement;
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
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateLayeredPartitionedTablesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateMaterializedViewStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateNotSuitableIndexForForeignKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartialIndexForForeignKeyInPartitionedTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartialIndexForForeignKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedIndexWithUnnecessaryWhereClauseStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableForBloatStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithBlobTypeColumnStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithDroppedColumnStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithInconsistentTypesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithIncrementingColumnsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithJsonAndSerialColumnsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreatePartitionedTableWithNoDataStatement;
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
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithBlobTypeColumnsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithCheckConstraintOnSerialPrimaryKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithColumnOfBigSerialTypeStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithFixedLengthVarcharStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithIdentityPrimaryKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithIncrementingColumnsStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithInheritanceStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithNaturalKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithSerialPrimaryKeyReferencesToAnotherTableStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithTimestampInTheMiddleStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithUniqueSerialColumnStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTableWithoutPrimaryKeyStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateTablesWithInconsistentTypesStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateUnloggedSequenceStatement;
import io.github.mfvanek.pg.core.fixtures.support.statements.CreateUnloggedTableStatement;
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

/**
 * Builder and populator for setting up database structures and test fixtures in PostgreSQL.
 */
@SuppressWarnings({"checkstyle:ClassDataAbstractionCoupling", "checkstyle:ClassFanOutComplexity", "PMD.ExcessivePublicCount", "PMD.CyclomaticComplexity"})
public final class DatabasePopulator implements AutoCloseable {

    private final DataSource dataSource;
    private final String schemaName;
    private final Map<Integer, Runnable> actionsToExecuteOutsideTransaction = new TreeMap<>();
    private final Map<Integer, DbStatement> statementsToExecuteInSameTransaction = new TreeMap<>();
    private final boolean supportsProcedures;
    private final boolean supportsUnloggedSequences;

    private DatabasePopulator(final DataSource dataSource,
                              final String schemaName,
                              final boolean supportsProcedures,
                              final boolean supportsUnloggedSequences) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.schemaName = Validators.notBlank(schemaName, "schemaName");
        this.supportsProcedures = supportsProcedures;
        this.supportsUnloggedSequences = supportsUnloggedSequences;
        register(1, new CreateSchemaStatement());
        register(2, new CreateClientsTableStatement());
        register(3, new CreateAccountsTableStatement());
    }

    static DatabasePopulator builder(final DataSource dataSource,
                                     final String schemaName,
                                     final boolean supportsProcedures,
                                     final boolean supportsUnloggedSequences) {
        return new DatabasePopulator(dataSource, schemaName, supportsProcedures, supportsUnloggedSequences);
    }

    /**
     * Configures the database with a custom collation.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withCustomCollation() {
        return register(4, new CreateCustomCollationStatement());
    }

    /**
     * Configures foreign key references between accounts and clients.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withReferences() {
        return register(5, new AddLinksBetweenAccountsAndClientsStatement());
    }

    /**
     * Populates tables with test data.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withData() {
        actionsToExecuteOutsideTransaction.putIfAbsent(20, new InsertDataIntoTablesAction(dataSource, schemaName));
        return this;
    }

    /**
     * Configures an invalid index.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withInvalidIndex() {
        actionsToExecuteOutsideTransaction.putIfAbsent(30, this::createInvalidIndex);
        return this;
    }

    /**
     * Configures duplicated B-tree indexes.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withDuplicatedIndex() {
        return register(40, new CreateDuplicatedIndexStatement());
    }

    /**
     * Configures duplicated hash indexes.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withDuplicatedHashIndex() {
        return register(41, new CreateDuplicatedHashIndexStatement());
    }

    /**
     * Configures a non-suitable index for a foreign key.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withNonSuitableIndex() {
        return register(43, new CreateNotSuitableIndexForForeignKeyStatement());
    }

    /**
     * Configures a suitable index for a foreign key.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withSuitableIndex() {
        return register(44, new CreateSuitableIndexForForeignKeyStatement());
    }

    /**
     * Configures a partial index for a foreign key.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withPartialIndexForForeignKey() {
        return register(45, new CreatePartialIndexForForeignKeyStatement());
    }

    /**
     * Configures a table without a primary key.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withTableWithoutPrimaryKey() {
        return register(47, new CreateTableWithoutPrimaryKeyStatement());
    }

    /**
     * Configures an index with nullable columns containing null values.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withNullValuesInIndex() {
        return register(48, new CreateIndexWithNullValuesStatement());
    }

    /**
     * Configures an index on boolean columns.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withBooleanValuesInIndex() {
        return register(49, new CreateIndexWithBooleanValuesStatement());
    }

    /**
     * Configures indexes using different operator classes.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withDifferentOpclassIndexes() {
        return register(50, new CreateIndexesWithDifferentOpclassStatement());
    }

    /**
     * Configures a materialized view.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withMaterializedView() {
        return register(55, new CreateMaterializedViewStatement());
    }

    /**
     * Configures duplicated indexes with custom collation.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withDuplicatedCustomCollationIndex() {
        return register(58, new CreateDuplicatedCustomCollationIndexStatement());
    }

    /**
     * Configures foreign keys on nullable columns in {@code bad_clients}.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withForeignKeyOnNullableColumn() {
        return register(60, new CreateForeignKeyOnNullableColumnStatement())
            .withTableWithoutPrimaryKey();
    }

    /**
     * Adds comments on database tables.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withCommentOnTables() {
        return register(64, new AddCommentOnTablesStatement());
    }

    /**
     * Adds blank comments on database tables.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withBlankCommentOnTables() {
        return register(65, new AddBlankCommentOnTablesStatement());
    }

    /**
     * Adds comments on table columns.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withCommentOnColumns() {
        return register(66, new AddCommentOnColumnsStatement());
    }

    /**
     * Adds blank comments on table columns.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withBlankCommentOnColumns() {
        return register(67, new AddBlankCommentOnColumnsStatement());
    }

    /**
     * Configures a table with columns converted to JSON type.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withJsonType() {
        return register(25, new ConvertColumnToJsonTypeStatement());
    }

    /**
     * Drops the info column from the clients table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withDroppedInfoColumn() {
        return register(70, new DropColumnStatement("clients", "info"));
    }

    /**
     * Configures a table with bigserial column type.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withSerialType() {
        return register(75, new CreateTableWithColumnOfBigSerialTypeStatement());
    }

    /**
     * Drops a serial column from bad_accounts table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withDroppedSerialColumn() {
        return register(76, new DropColumnStatement("bad_accounts", "real_account_id"));
    }

    /**
     * Configures a table with check constraint on serial primary key.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withCheckConstraintOnSerialPrimaryKey() {
        return register(80, new CreateTableWithCheckConstraintOnSerialPrimaryKeyStatement());
    }

    /**
     * Configures a table with a unique constraint on serial column.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withUniqueConstraintOnSerialColumn() {
        return register(81, new CreateTableWithUniqueSerialColumnStatement());
    }

    /**
     * Configures a table with serial primary key referencing another table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withSerialPrimaryKeyReferencesToAnotherTable() {
        return register(82, new CreateTableWithSerialPrimaryKeyReferencesToAnotherTableStatement())
            .withCheckConstraintOnSerialPrimaryKey()
            .withUniqueConstraintOnSerialColumn();
    }

    /**
     * Configures database functions.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withFunctions() {
        return register(90, new CreateFunctionsStatement());
    }

    /**
     * Configures database stored procedures if supported.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withProcedures() {
        if (supportsProcedures) {
            return register(91, new CreateProceduresStatement());
        }
        return this;
    }

    /**
     * Adds blank comments on database functions.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withBlankCommentOnFunctions() {
        return register(92, new AddBlankCommentOnFunctionsStatement());
    }

    /**
     * Adds comments on database functions.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withCommentOnFunctions() {
        return register(93, new AddCommentOnFunctionsStatement());
    }

    /**
     * Adds comments on database stored procedures if supported.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withCommentOnProcedures() {
        if (supportsProcedures) {
            return register(94, new AddCommentOnProceduresStatement());
        }
        return this;
    }

    /**
     * Configures invalid foreign key constraints.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withNotValidConstraints() {
        return register(95, new AddInvalidForeignKeyStatement());
    }

    /**
     * Configures B-tree indexes on array columns.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withBtreeIndexesOnArrayColumn() {
        return register(96, new CreateIndexesOnArrayColumnStatement());
    }

    /**
     * Configures a sequence approaching maximum value.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withSequenceOverflow() {
        return register(97, new CreateSequenceStatement());
    }

    /**
     * Configures a table with an identity column as primary key.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withIdentityPrimaryKey() {
        return register(98, new CreateTableWithIdentityPrimaryKeyStatement());
    }

    /**
     * Configures duplicated foreign key constraints.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withDuplicatedForeignKeys() {
        return register(105, new AddDuplicatedForeignKeysStatement());
    }

    /**
     * Configures intersected foreign key constraints.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withIntersectedForeignKeys() {
        return register(106, new AddIntersectedForeignKeysStatement());
    }

    /**
     * Configures a partitioned table without comments.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withPartitionedTableWithoutComments() {
        return register(110, new CreatePartitionedTableWithoutCommentsStatement());
    }

    /**
     * Configures a partitioned table without a primary key.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withPartitionedTableWithoutPrimaryKey() {
        return register(111, new CreatePartitionedTableWithoutPrimaryKeyStatement());
    }

    /**
     * Configures a primary key for the default partition.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withPrimaryKeyForDefaultPartition() {
        return register(112, new AddPrimaryKeyForDefaultPartitionStatement());
    }

    /**
     * Configures a partitioned table with nullable fields and indexes.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withNullableIndexesInPartitionedTable() {
        return register(113, new CreatePartitionedTableWithNullableFieldsStatement());
    }

    /**
     * Configures a partitioned table with very long names.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withVeryLongNamesInPartitionedTable() {
        return register(114, new CreatePartitionedTableWithVeryLongNamesStatement());
    }

    /**
     * Configures a partitioned table with JSON and serial columns.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withJsonAndSerialColumnsInPartitionedTable() {
        return register(115, new CreatePartitionedTableWithJsonAndSerialColumnsStatement());
    }

    /**
     * Configures a partitioned table with serial and foreign key constraints.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withSerialAndForeignKeysInPartitionedTable() {
        return withDictTable()
            .register(116, new CreatePartitionedTableWithSerialAndForeignKeysStatement());
    }

    /**
     * Configures duplicated and intersected indexes on a partitioned table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withDuplicatedAndIntersectedIndexesInPartitionedTable() {
        return register(117, new CreateDuplicatedAndIntersectedIndexesInPartitionedTableStatement());
    }

    /**
     * Configures invalid constraints on a partitioned table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withNotValidConstraintInPartitionedTable() {
        return register(118, new AddNotValidConstraintToPartitionedTableStatement());
    }

    /**
     * Configures an array column with a B-tree index on a partitioned table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withBtreeIndexOnArrayColumnInPartitionedTable() {
        return register(119, new AddArrayColumnAndIndexToPartitionedTableStatement());
    }

    /**
     * Configures duplicated foreign keys on a partitioned table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withDuplicatedForeignKeysInPartitionedTable() {
        return withDictTable()
            .register(120, new AddDuplicatedForeignKeysToPartitionedTableStatement());
    }

    /**
     * Configures intersected foreign keys on a partitioned table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withIntersectedForeignKeysInPartitionedTable() {
        return withDictTable()
            .register(121, new AddIntersectedForeignKeysToPartitionedTableStatement());
    }

    /**
     * Configures a partitioned table with a dropped column.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withDroppedColumnInPartitionedTable() {
        return register(122, new CreatePartitionedTableWithDroppedColumnStatement());
    }

    /**
     * Configures a badly named partitioned table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withBadlyNamedPartitionedTable() {
        return register(123, new CreateBadlyNamedPartitionedTableStatement());
    }

    /**
     * Configures a partitioned table with varchar columns.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withVarcharInPartitionedTable() {
        return register(124, new CreatePartitionedTableWithVarcharStatement());
    }

    /**
     * Configures a partitioned index with an unnecessary WHERE clause.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withUnnecessaryWhereClauseInPartitionedIndex() {
        return register(125, new CreatePartitionedIndexWithUnnecessaryWhereClauseStatement());
    }

    /**
     * Configures an empty database table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withEmptyTable() {
        return register(130, new CreateEmptyTableStatement());
    }

    /**
     * Configures database objects with unconventional names.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withBadlyNamedObjects() {
        return register(135, new CreateBadlyNamedObjectsStatement());
    }

    /**
     * Configures tables storing UUID strings in {@code varchar(32)} and {@code varchar(36)} columns.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withVarcharInsteadOfUuid() {
        return register(136, new CreateTableWithFixedLengthVarcharStatement());
    }

    /**
     * Drops the account number column from accounts table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withDroppedAccountNumberColumn() {
        return register(137, new DropColumnStatement("accounts", "account_number"));
    }

    /**
     * Configures an index with an unnecessary WHERE clause.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withUnnecessaryWhereClause() {
        return register(138, new CreateIndexWithUnnecessaryWhereClauseStatement());
    }

    /**
     * Configures a table using natural keys.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withNaturalKeys() {
        return register(139, new CreateTableWithNaturalKeyStatement());
    }

    /**
     * Configures a table with a money column.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withMoneyColumn() {
        return register(140, new AddMoneyColumnStatement());
    }

    /**
     * Configures a table with a timestamp column in the middle of columns.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withTimestampInTheMiddle() {
        return register(141, new CreateTableWithTimestampInTheMiddleStatement());
    }

    /**
     * Configures a table where all columns are nullable.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withTableWhereAllColumnsNullable() {
        return register(144, new CreateTableWhereAllColumnsNullableStatement());
    }

    /**
     * Configures a partitioned table containing index bloat.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withBloatInPartitionedTable() {
        return register(145, new CreatePartitionedTableForBloatStatement());
    }

    /**
     * Configures tables using table inheritance.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withInheritance() {
        return register(146, new CreateTableWithInheritanceStatement());
    }

    /**
     * Configures a dictionary lookup table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withDictTable() {
        return register(31, new CreateDictTableStatement());
    }

    /**
     * Configures a composite foreign key with null values.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withCompositeForeignKey() {
        return withDictTable()
            .register(147, new AddCompositeForeignKeyWithNullValuesStatement());
    }

    /**
     * Configures a composite foreign key with null values in a partitioned table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withCompositeForeignKeyInPartitionedTable() {
        return withDictTable()
            .register(148, new AddCompositeForeignKeyWithNullValuesToPartitionedTableStatement());
    }

    /**
     * Configures an empty partitioned table without data.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withEmptyPartitionedTable() {
        return register(149, new CreatePartitionedTableWithNoDataStatement());
    }

    /**
     * Configures layered partitioned tables.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withLayeredPartitionedTables() {
        return register(150, new CreateLayeredPartitionedTablesStatement());
    }

    /**
     * Configures self-referencing foreign keys.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withSelfReferencedForeignKeys() {
        return register(151, new AddSelfReferencedForeignKeysStatement());
    }

    /**
     * Configures self-referencing foreign keys in a partitioned table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withSelfReferencedForeignKeysInPartitionedTable() {
        return register(152, new AddSelfReferencedForeignKeysToPartitionedTableStatement());
    }

    /**
     * Configures a table containing blob type columns.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withBlobTypeColumns() {
        return register(153, new CreateTableWithBlobTypeColumnsStatement());
    }

    /**
     * Configures a partitioned table containing a blob type column.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withBlobTypeColumnInPartitionedTable() {
        return register(154, new CreatePartitionedTableWithBlobTypeColumnStatement());
    }

    /**
     * Configures a table with a {@code bigint} identity primary key.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withIncrementingColumns() {
        return register(155, new CreateTableWithIncrementingColumnsStatement());
    }

    /**
     * Configures a date-partitioned table with a plain {@code bigint} ID.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withIncrementingColumnsInPartitionedTable() {
        return register(156, new CreatePartitionedTableWithIncrementingColumnsStatement());
    }

    /**
     * Configures tables with inconsistent column types across related columns.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withInconsistentTypes() {
        return register(157, new CreateTablesWithInconsistentTypesStatement());
    }

    /**
     * Configures a partitioned table with inconsistent column types.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withInconsistentTypesInPartitionedTable() {
        return register(158, new CreatePartitionedTableWithInconsistentTypesStatement());
    }

    /**
     * Configures an unlogged database table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withUnloggedTable() {
        return register(159, new CreateUnloggedTableStatement());
    }

    /**
     * Configures an unlogged sequence if supported.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withUnloggedSequence() {
        if (supportsUnloggedSequences) {
            return register(160, new CreateUnloggedSequenceStatement());
        }
        return this;
    }

    /**
     * Configures a partial index for a foreign key in a partitioned table.
     *
     * @return this {@code DatabasePopulator} instance
     */
    public DatabasePopulator withPartialIndexForForeignKeyInPartitionedTable() {
        return withSerialAndForeignKeysInPartitionedTable()
            .register(161, new CreatePartialIndexForForeignKeyInPartitionedTableStatement());
    }

    /**
     * Executes all registered statements and actions to populate the database schema.
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        ExecuteUtils.executeOnDatabase(dataSource, statement -> statement.execute(String.format(Locale.ROOT, "drop schema if exists %s cascade", schemaName)));
    }
}
