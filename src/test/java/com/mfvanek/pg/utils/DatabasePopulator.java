/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.utils;

import org.apache.commons.lang3.RandomStringUtils;

import javax.annotation.Nonnull;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
        createTableDocuments();
        addLinksBetweenAccountsAndClients();
        addLinksBetweenDocumentsAndAccounts();
        insertDataIntoTables();
    }

    public void populateOnlyTablesAndReferences() {
        // TODO do it inside transaction
        createTableClients();
        createTableAccounts();
        createTableDocuments();
        addLinksBetweenAccountsAndClients();
        addLinksBetweenDocumentsAndAccounts();
    }

    public void createInvalidIndex() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("create unique index concurrently if not exists i_clients_last_name_first_name " +
                    "on clients (last_name, first_name)");
        } catch (SQLException e) {
            // TODO logging
            // do nothing
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
    }

    private void createTableDocuments() {
    }

    private void addLinksBetweenAccountsAndClients() {

    }

    private void addLinksBetweenDocumentsAndAccounts() {

    }

    private void insertDataIntoTables() {
        final int clientsCountToCreate = 1_000;
        final String insertClientSql = "insert into clients (first_name, last_name) values (?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(insertClientSql)) {
            for (int counter = 0; counter < clientsCountToCreate; ++counter) {
                final String lastName = RandomStringUtils.randomAlphabetic(10);
                final String firstName = RandomStringUtils.randomAlphabetic(10);
                statement.setString(1, firstName);
                statement.setString(2, lastName);
                statement.executeUpdate();
            }
            // Insert at least one duplicated client row
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("truncate table clients cascade");
            statement.execute("drop table if exists clients");
            statement.execute("drop sequence if exists clients_seq");
        }
    }
}
