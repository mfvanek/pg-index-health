/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static io.github.mfvanek.pg.utils.TestUtils.executeInTransaction;
import static io.github.mfvanek.pg.utils.TestUtils.executeOnDatabase;

public final class DatabasePopulator implements AutoCloseable {

    private final DataSource dataSource;
    private String schemaName = "public";
    private final Map<Integer, Runnable> actions;

    private DatabasePopulator(@Nonnull final DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.actions = new TreeMap<>();
        this.actions.putIfAbsent(1, this::createSchema);
        this.actions.putIfAbsent(2, this::createTableClients);
        this.actions.putIfAbsent(3, this::createTableAccounts);
    }

    static DatabasePopulator builder(@Nonnull final DataSource dataSource) {
        return new DatabasePopulator(dataSource);
    }

    @Nonnull
    public DatabasePopulator withReferences() {
        actions.putIfAbsent(10, this::addLinksBetweenAccountsAndClients);
        return this;
    }

    @Nonnull
    public DatabasePopulator withData() {
        actions.putIfAbsent(20, this::insertDataIntoTables);
        return this;
    }

    @Nonnull
    public DatabasePopulator withInvalidIndex() {
        actions.putIfAbsent(30, this::createInvalidIndex);
        return this;
    }

    @Nonnull
    public DatabasePopulator withDuplicatedIndex() {
        actions.putIfAbsent(40, this::createDuplicatedIndex);
        return this;
    }

    @Nonnull
    public DatabasePopulator withDuplicatedHashIndex() {
        this.actions.putIfAbsent(50, this::createDuplicatedHashIndex);
        return this;
    }

    @Nonnull
    public DatabasePopulator withNonSuitableIndex() {
        actions.putIfAbsent(100, this::createNotSuitableIndexForForeignKey);
        return this;
    }

    @Nonnull
    public DatabasePopulator withSuitableIndex() {
        actions.putIfAbsent(110, this::createSuitableIndexForForeignKey);
        return this;
    }

    @Nonnull
    public DatabasePopulator withTableWithoutPrimaryKey() {
        actions.putIfAbsent(150, this::createTableWithoutPrimaryKey);
        return this;
    }

    @Nonnull
    public DatabasePopulator withNullValuesInIndex() {
        actions.putIfAbsent(160, this::createIndexWithNulls);
        return this;
    }

    @Nonnull
    DatabasePopulator withSchema(@Nonnull final String schemaName) {
        this.schemaName = Validators.notBlank(schemaName, "schemaName");
        return this;
    }

    @Nonnull
    public DatabasePopulator withStatistics() {
        // should be the last step in pipeline
        actions.putIfAbsent(Integer.MAX_VALUE, this::collectStatistics);
        return this;
    }

    @Nonnull
    public DatabasePopulator withDifferentOpclassIndexes() {
        actions.putIfAbsent(200, this::createIndexesWithDifferentOpclass);
        return this;
    }

    @Nonnull
    public DatabasePopulator withMaterializedView() {
        actions.putIfAbsent(210, this::createMaterializedView);
        return this;
    }

    @Nonnull
    public DatabasePopulator withCustomCollation() {
        actions.putIfAbsent(4, this::createCustomCollation);
        return this;
    }

    @Nonnull
    public DatabasePopulator withDuplicatedCustomCollationIndex() {
        this.actions.putIfAbsent(220, this::createDuplicatedCustomCollationIndex);
        return this;
    }

    public void populate() {
        actions.forEach((k, v) -> v.run());
    }

    private void createSchema() {
        executeOnDatabase(dataSource, statement -> {
            statement.execute("create schema if not exists " + schemaName);
            final String checkQuery = String.format(
                    "select exists(select 1 from information_schema.schemata where schema_name = '%s')", schemaName);
            try (ResultSet rs = statement.executeQuery(checkQuery)) {
                if (rs.next()) {
                    final boolean schemaExists = rs.getBoolean(1);
                    if (schemaExists) {
                        return;
                    }
                }
                throw new RuntimeException("Schema with name " + schemaName + " wasn't created");
            }
        });
    }

    private void createInvalidIndex() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(String.format("create unique index concurrently if not exists " +
                    "i_clients_last_name_first_name on %s.clients (last_name, first_name)", schemaName));
        } catch (SQLException e) {
            // do nothing, just skip error
        }
    }

    private void createDuplicatedIndex() {
        executeOnDatabase(dataSource, statement -> {
            statement.execute(String.format("create index concurrently if not exists i_accounts_account_number " +
                    "on %s.accounts (account_number)", schemaName));
            statement.execute(String.format("create index concurrently if not exists i_accounts_account_number_not_deleted " +
                    "on %s.accounts (account_number) where not deleted", schemaName));
            statement.execute(String.format("create index concurrently if not exists i_accounts_number_balance_not_deleted " +
                    "on %s.accounts (account_number, account_balance) where not deleted", schemaName));
            statement.execute(String.format("create index concurrently if not exists i_clients_last_first " +
                    "on %s.clients (last_name, first_name)", schemaName));
            statement.execute(String.format("create index concurrently if not exists i_clients_last_name " +
                    "on %s.clients (last_name)", schemaName));
            statement.execute(String.format("create index concurrently if not exists i_accounts_id_account_number_not_deleted " +
                    "on %s.accounts (id, account_number) where not deleted", schemaName));
        });
    }

    private void createDuplicatedHashIndex() {
        executeOnDatabase(dataSource, statement -> {
            statement.execute(String.format("create index concurrently if not exists i_accounts_account_number " +
                    "on %s.accounts using hash(account_number)", schemaName));
            statement.execute(String.format("create index concurrently if not exists i_clients_last_first " +
                    "on %s.clients (last_name, first_name)", schemaName));
            statement.execute(String.format("create index concurrently if not exists i_clients_last_name " +
                    "on %s.clients using hash(last_name)", schemaName));
        });
    }

    private void createIndexWithNulls() {
        executeOnDatabase(dataSource, statement ->
                statement.execute(String.format("create index concurrently if not exists i_clients_middle_name " +
                        "on %s.clients (middle_name)", schemaName)));
    }

    private void createNotSuitableIndexForForeignKey() {
        executeOnDatabase(dataSource, statement ->
                statement.execute(String.format("create index concurrently if not exists " +
                        "i_accounts_account_number_client_id on %s.accounts (account_number, client_id)", schemaName)));
    }

    private void createSuitableIndexForForeignKey() {
        executeOnDatabase(dataSource, statement ->
                statement.execute(String.format("create index concurrently if not exists " +
                        "i_accounts_client_id_account_number on %s.accounts (client_id, account_number)", schemaName)));
    }

    private void createTableClients() {
        executeInTransaction(dataSource, statement -> {
            statement.execute(String.format("create sequence if not exists %s.clients_seq", schemaName));
            statement.execute(String.format("create table if not exists %s.clients (" +
                    "id bigint not null primary key default nextval('%s.clients_seq')," +
                    "last_name varchar(255) not null," +
                    "first_name varchar(255) not null," +
                    "middle_name varchar(255))", schemaName, schemaName));
        });
    }

    private void createTableAccounts() {
        executeInTransaction(dataSource, statement -> {
            statement.execute(String.format("create sequence if not exists %s.accounts_seq", schemaName));
            statement.execute(String.format("create table if not exists %s.accounts (" +
                    "id bigint not null primary key default nextval('%s.accounts_seq')," +
                    "client_id bigint not null," +
                    "account_number varchar(50) not null unique," +
                    "account_balance numeric(22,2) not null default 0," +
                    "deleted boolean not null default false)", schemaName, schemaName));
        });
    }

    private void addLinksBetweenAccountsAndClients() {
        executeOnDatabase(dataSource, statement ->
                statement.execute(String.format("alter table if exists %s.accounts " +
                                "add constraint c_accounts_fk_client_id foreign key (client_id) references %s.clients (id);",
                        schemaName, schemaName)));
    }

    private void insertDataIntoTables() {
        final int clientsCountToCreate = 1_000;
        final String insertClientSql = String.format(
                "insert into %s.clients (id, first_name, last_name) values (?, ?, ?)", schemaName);
        final String insertAccountSql = String.format(
                "insert into %s.accounts (client_id, account_number) values (?, ?)", schemaName);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertClientStatement = connection.prepareStatement(insertClientSql);
             PreparedStatement insertAccountStatement = connection.prepareStatement(insertAccountSql)) {
            connection.setAutoCommit(false);
            for (int counter = 0; counter < clientsCountToCreate; ++counter) {
                final long clientId = getNextClientIdFromSequence(connection);
                final String lastName = RandomStringUtils.randomAlphabetic(10);
                final String firstName = RandomStringUtils.randomAlphabetic(10);
                insertClientStatement.setLong(1, clientId);
                insertClientStatement.setString(2, firstName);
                insertClientStatement.setString(3, lastName);
                insertClientStatement.executeUpdate();

                final String accountNumber = generateAccountNumber(clientId);
                insertAccountStatement.setLong(1, clientId);
                insertAccountStatement.setString(2, accountNumber);
                insertAccountStatement.executeUpdate();
            }
            // Insert at least one duplicated client row
            final long clientId = getNextClientIdFromSequence(connection);
            insertClientStatement.setLong(1, clientId);
            insertClientStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateAccountNumber(final long clientId) {
        return "407028101" + StringUtils.leftPad(String.valueOf(clientId), 11, '0');
    }

    private long getNextClientIdFromSequence(@Nonnull final Connection connection) {
        final String selectClientIdSql = String.format("select nextval('%s.clients_seq')", schemaName);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectClientIdSql)) {
            if (resultSet.next()) {
                return resultSet.getLong(1);
            } else {
                throw new RuntimeException("An error occurred while retrieving a value from the sequence");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        executeOnDatabase(dataSource, statement -> {
            statement.execute(String.format("drop table if exists %s.accounts cascade", schemaName));
            statement.execute(String.format("drop sequence if exists %s.accounts_seq", schemaName));

            statement.execute(String.format("drop table if exists %s.clients", schemaName));
            statement.execute(String.format("drop sequence if exists %s.clients_seq", schemaName));

            statement.execute(String.format("drop table if exists %s.bad_clients", schemaName));
        });
    }

    private void createTableWithoutPrimaryKey() {
        executeOnDatabase(dataSource, statement -> {
            statement.execute(String.format("create table if not exists %s.bad_clients (" +
                    "id bigint not null, " +
                    "name varchar(255) not null)", schemaName));
            final String checkQuery = String.format("select exists (%n" +
                    "   select 1 %n" +
                    "   from pg_catalog.pg_class c%n" +
                    "   join pg_catalog.pg_namespace n on n.oid = c.relnamespace%n" +
                    "   where n.nspname = '%s'%n" +
                    "   and c.relname = '%s'%n" +
                    "   and c.relkind = 'r'%n" +
                    "   );", schemaName, "bad_clients");
            try (ResultSet rs = statement.executeQuery(checkQuery)) {
                if (rs.next()) {
                    final boolean schemaExists = rs.getBoolean(1);
                    if (schemaExists) {
                        return;
                    }
                }
                throw new RuntimeException("Table with name 'bad_clients' in schema " + schemaName + " wasn't created");
            }
        });
    }

    private void collectStatistics() {
        collectStatistics(dataSource, schemaName);
    }

    static void collectStatistics(@Nonnull final DataSource dataSource, @Nonnull final String schemaName) {
        executeOnDatabase(dataSource, statement -> {
            final String query = String.format("vacuum analyze %s.", schemaName);
            statement.execute(query + "accounts");
            statement.execute(query + "clients");
        });
    }

    private void createIndexesWithDifferentOpclass() {
        executeOnDatabase(dataSource, statement -> {
            statement.execute(String.format("create index concurrently if not exists i_clients_last_name " +
                    "on %s.clients using btree(lower(last_name))", schemaName));
            statement.execute(String.format("create index concurrently if not exists i_clients_last_name_ops " +
                    "on %s.clients using btree(lower(last_name) text_pattern_ops)", schemaName));
        });
    }

    private void createMaterializedView() {
        executeOnDatabase(dataSource, statement ->
                statement.execute(String.format("create materialized view if not exists %s.accounts_mat_view as (" +
                        "select client_id, account_number from %s.accounts);", schemaName, schemaName)));
    }

    private void createCustomCollation() {
        executeOnDatabase(dataSource, statement -> {
            final String customCollation = "C.UTF-8";
            if (isCollationExist(statement, customCollation)) {
                return;
            }
            createCustomCollation(statement, customCollation);
        });
    }

    private boolean isCollationExist(@Nonnull final Statement statement, @Nonnull final String collation) {
        final String sqlQuery = "select exists(select 1 from pg_catalog.pg_collation as pgc where pgc.collname = '%s'::text)";
        try (ResultSet rs = statement.executeQuery(String.format(sqlQuery, collation))) {
            rs.next();
            return rs.getBoolean(1);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void createCustomCollation(@Nonnull final Statement statement,
                                       @Nonnull final String customCollation) throws SQLException {
        final String query = "create collation \"%s\" from \"%s\";";
        final String systemLocale;
        if (SystemUtils.IS_OS_WINDOWS) {
            final String icuCollation = "en-US-x-icu";
            if (isCollationExist(statement, icuCollation)) {
                systemLocale = icuCollation; // for Pg 10+
            } else {
                systemLocale = "C"; // for Pg 9.6
            }
        } else {
            if (SystemUtils.IS_OS_LINUX) {
                systemLocale = "en_US.utf8";
            } else if (SystemUtils.IS_OS_MAC) {
                systemLocale = "en_US.UTF-8";
            } else {
                throw new RuntimeException("Unsupported operation system");
            }
        }
        statement.execute(String.format(query, customCollation, systemLocale));
    }

    private void createDuplicatedCustomCollationIndex() {
        executeOnDatabase(dataSource, statement -> {
            statement.execute(String.format("create index concurrently if not exists i_accounts_account_number " +
                    "on %s.accounts (account_number collate \"C.UTF-8\")", schemaName));
        });
    }
}
