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

import javax.annotation.Nonnull;

public final class NamedParametersParser {

    private final String originalSqlQuery;
    private final int queryLength;
    private final StringBuilder resultQuery;

    private boolean isParsed = false;
    private boolean isInSingleQuotes = false;
    private boolean isInDoubleQuotes = false;
    private boolean isPartOfSingleLineComment = false;
    private boolean isPartOfMultiLineComment = false;
    private boolean isDoubleColon = false;

    private NamedParametersParser(@Nonnull final String originalSqlQuery) {
        this.originalSqlQuery = Validators.notBlank(originalSqlQuery, "originalSqlQuery");
        this.queryLength = originalSqlQuery.length();
        this.resultQuery = new StringBuilder(queryLength);
    }

    @Nonnull
    private String parse() {
        if (isParsed) {
            return resultQuery.toString();
        }
        return doParse();
    }

    @Nonnull
    private String doParse() {
        for (int i = 0; i < queryLength; ++i) {
            char c = originalSqlQuery.charAt(i);
            if (isInSingleQuotes) {
                if (c == '\'') {
                    isInSingleQuotes = false;
                }
            } else if (isInDoubleQuotes) {
                if (c == '"') {
                    isInDoubleQuotes = false;
                }
            } else if (isPartOfMultiLineComment) {
                if (c == '*' && nextChar(i) == '/') {
                    isPartOfMultiLineComment = false;
                }
            } else if (isDoubleColon) {
                if (!Character.isJavaIdentifierPart(c)) {
                    isDoubleColon = false;
                }
            } else if (isPartOfSingleLineComment) {
                if (c == '\n') {
                    isPartOfSingleLineComment = false;
                }
            } else {
                if (c == '\'') {
                    isInSingleQuotes = true;
                } else if (c == '"') {
                    isInDoubleQuotes = true;
                } else if (c == '/' && nextChar(i) == '*') {
                    isPartOfMultiLineComment = true;
                } else if (c == '-' && nextChar(i) == '-') {
                    isPartOfSingleLineComment = true;
                } else if (c == ':' && nextChar(i) == ':') {
                    isDoubleColon = true;
                } else if (c == ':' && i + 1 < queryLength && Character.isJavaIdentifierStart(nextChar(i))) {
                    int j = i + 2;
                    while (j < queryLength && Character.isJavaIdentifierPart(originalSqlQuery.charAt(j))) {
                        ++j;
                    }
                    c = '?'; // replace the parameter with a question mark
                    i = j - 1; // skip past the end if the parameter
                }
            }
            resultQuery.append(c);
        }

        isParsed = true;
        return resultQuery.toString();
    }

    private char nextChar(final int currentPosition) {
        return originalSqlQuery.charAt(currentPosition + 1);
    }

    @Nonnull
    public static String parse(@Nonnull final String originalSqlQuery) {
        return new NamedParametersParser(originalSqlQuery).parse();
    }
}
