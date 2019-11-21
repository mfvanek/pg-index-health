/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public final class DatabasePopulator implements AutoCloseable {

    private final DataSource dataSource;

    public DatabasePopulator(@Nonnull final DataSource dataSource) {
        this.dataSource = Objects.requireNonNull(dataSource);
    }

    public void populateWithDataAndReferences() {
        // TODO do it inside transaction
        createTableClients();
        createTableAccounts();
        addLinksBetweenAccountsAndClients();
        insertDataIntoTables();
    }

    public void populateOnlyTablesAndReferences() {
        // TODO do it inside transaction
        createTableClients();
        createTableAccounts();
        addLinksBetweenAccountsAndClients();
    }

    public void populateOnlyTables() {
        // TODO do it inside transaction
        createTableClients();
        createTableAccounts();
    }

    public void createInvalidIndex() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("create unique index concurrently if not exists i_clients_last_name_first_name " +
                    "on clients (last_name, first_name)");
        } catch (SQLException e) {
            // do nothing, just skip error
        }
    }

    public void createDuplicatedIndex() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("create index concurrently if not exists i_accounts_account_number " +
                    "on accounts (account_number)");
            statement.execute("create index concurrently if not exists i_clients_last_first " +
                    "on clients (last_name, first_name)");
            statement.execute("create index concurrently if not exists i_clients_last_name " +
                    "on clients (last_name)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createIndexWithNulls() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("create index concurrently if not exists i_clients_middle_name " +
                    "on clients (middle_name)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createNotSuitableIndexForForeignKey() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("create index concurrently if not exists i_accounts_account_number_client_id " +
                    "on accounts (account_number, client_id)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createSuitableIndexForForeignKey() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("create index concurrently if not exists i_accounts_client_id_account_number " +
                    "on accounts (client_id, account_number)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTableClients() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("create sequence if not exists clients_seq");
            statement.execute("create table if not exists clients (" +
                    "id bigint not null primary key default nextval('clients_seq'), " +
                    "last_name varchar(255) not null, " +
                    "first_name varchar(255) not null, " +
                    "middle_name varchar(255))");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createTableAccounts() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("create sequence accounts_seq");
            statement.execute("create table accounts (" +
                    "id bigint not null primary key default nextval('accounts_seq'), " +
                    "client_id bigint not null," +
                    "account_number varchar(50) not null unique, " +
                    "account_balance numeric(22,2) not null default 0)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void addLinksBetweenAccountsAndClients() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("alter table if exists accounts " +
                    "add constraint c_accounts_fk_client_id foreign key (client_id) references clients (id);");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertDataIntoTables() {
        final int clientsCountToCreate = 1_000;
        final String insertClientSql = "insert into clients (id, first_name, last_name) values (?, ?, ?)";
        final String insertAccountSql = "insert into accounts (client_id, account_number) values (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertClientStatement = connection.prepareStatement(insertClientSql);
             PreparedStatement insertAccountStatement = connection.prepareStatement(insertAccountSql)) {
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateAccountNumber(final long clientId) {
        return "407028101" + StringUtils.leftPad(String.valueOf(clientId), 11, '0');
    }

    private long getNextClientIdFromSequence(@Nonnull final Connection connection) {
        final String selectClientIdSql = "select nextval('clients_seq')";
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
    public void close() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("truncate table accounts cascade");
            statement.execute("truncate table clients cascade");

            statement.execute("drop table if exists accounts");
            statement.execute("drop sequence if exists accounts_seq");

            statement.execute("drop table if exists clients");
            statement.execute("drop sequence if exists clients_seq");

            statement.execute("drop table if exists bad_clients");
        }
    }

    public void tryToFindAccountByClientId(final int amountOfTries) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            for (int counter = 0; counter < amountOfTries; ++counter) {
                statement.execute("select count(*) from accounts where client_id = 1::bigint");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createTableWithoutPrimaryKey() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("create table bad_clients (" +
                    "id bigint not null, " +
                    "name varchar(255) not null)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
