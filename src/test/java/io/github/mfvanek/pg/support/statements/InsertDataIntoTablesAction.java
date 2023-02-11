/*
 * Copyright (c) 2019-2023. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.support.statements;

import io.github.mfvanek.pg.utils.PgSqlException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.sql.DataSource;

public class InsertDataIntoTablesAction implements Runnable {

    private final DataSource dataSource;
    private final String schemaName;

    public InsertDataIntoTablesAction(@Nonnull final DataSource dataSource, @Nonnull final String schemaName) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.schemaName = Objects.requireNonNull(schemaName);
    }

    @Override
    public void run() {
        final int clientsCountToCreate = 1_000;
        final String insertClientSql = String.format(
                "insert into %s.clients (id, first_name, last_name, info) values (?, ?, ?, ?)", schemaName);
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
                insertClientStatement.setObject(4, prepareClientInfo());
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
            throw new PgSqlException(e);
        }
    }

    @Nonnull
    private static PGobject prepareClientInfo() throws SQLException {
        final PGobject clientInfo = new PGobject();
        clientInfo.setType("jsonb");
        clientInfo.setValue("{\"client\":{\"date\":\"2022-08-14T23:27:42\",\"result\":\"created\"}}");
        return clientInfo;
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
                throw new IllegalStateException("An error occurred while retrieving a value from the sequence");
            }
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }
}
