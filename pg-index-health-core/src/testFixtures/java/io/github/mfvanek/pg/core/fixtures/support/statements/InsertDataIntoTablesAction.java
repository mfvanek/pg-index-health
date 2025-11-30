/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.fixtures.support.statements;

import io.github.mfvanek.pg.connection.exception.PgSqlException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.postgresql.util.PGobject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;
import java.util.Objects;
import javax.sql.DataSource;

public class InsertDataIntoTablesAction implements Runnable {

    private final DataSource dataSource;
    private final String schemaName;

    public InsertDataIntoTablesAction(final DataSource dataSource, final String schemaName) {
        this.dataSource = Objects.requireNonNull(dataSource);
        this.schemaName = Objects.requireNonNull(schemaName);
    }

    @Override
    public void run() {
        final int clientsCountToCreate = 1_000;
        final String insertClientSql = String.format(
            Locale.ROOT, "insert into %s.clients (id, first_name, last_name, info, email, phone, gender) values (?, ?, ?, ?, ?, ?, ?)", schemaName);
        final String insertAccountSql = String.format(
            Locale.ROOT, "insert into %s.accounts (client_id, account_number) values (?, ?)", schemaName);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertClientStatement = connection.prepareStatement(insertClientSql);
             PreparedStatement insertAccountStatement = connection.prepareStatement(insertAccountSql)) {
            connection.setAutoCommit(false);
            for (int counter = 0; counter < clientsCountToCreate; ++counter) {
                final long clientId = getNextClientIdFromSequence(connection);
                final String lastName = RandomStringUtils.secureStrong().nextAlphabetic(15);
                final String firstName = RandomStringUtils.secureStrong().nextAlphabetic(10);
                final String domainName = RandomStringUtils.secureStrong().nextAlphabetic(8);
                final String email = lastName + "_" + firstName + "@" + domainName + ".com";
                final String phone = RandomStringUtils.secureStrong().nextAlphanumeric(10);
                final String gender = clientId % 2 == 0 ? "M" : "F";
                insertClientStatement.setLong(1, clientId);
                insertClientStatement.setString(2, firstName);
                insertClientStatement.setString(3, lastName);
                insertClientStatement.setObject(4, prepareClientInfo());
                insertClientStatement.setString(5, email);
                insertClientStatement.setString(6, phone);
                insertClientStatement.setString(7, gender);
                insertClientStatement.executeUpdate();

                insertAccount(insertAccountStatement, clientId);
            }
            // Insert at least one duplicated client row
            final long clientId = getNextClientIdFromSequence(connection);
            insertClientStatement.setLong(1, clientId);
            insertClientStatement.setString(6, clientId + "unique_phone");
            insertClientStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new PgSqlException(e);
        }
    }

    private void insertAccount(final PreparedStatement insertAccountStatement, final long clientId) throws SQLException {
        final String accountNumber = generateAccountNumber(clientId);
        insertAccountStatement.setLong(1, clientId);
        insertAccountStatement.setString(2, accountNumber);
        insertAccountStatement.executeUpdate();
    }

    private static PGobject prepareClientInfo() throws SQLException {
        final PGobject clientInfo = new PGobject();
        clientInfo.setType("jsonb");
        clientInfo.setValue("{\"client\":{\"date\":\"2022-08-14T23:27:42\",\"result\":\"created\"}}");
        return clientInfo;
    }

    private String generateAccountNumber(final long clientId) {
        return "407028101" + StringUtils.leftPad(String.valueOf(clientId), 11, '0');
    }

    private long getNextClientIdFromSequence(final Connection connection) {
        final String selectClientIdSql = String.format(Locale.ROOT, "select nextval('%s.clients_seq')", schemaName);
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
