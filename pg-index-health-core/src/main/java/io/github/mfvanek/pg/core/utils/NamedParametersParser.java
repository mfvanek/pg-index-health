/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core.utils;

import io.github.mfvanek.pg.model.validation.Validators;

@SuppressWarnings({"PMD.AvoidReassigningLoopVariables", "PMD.CognitiveComplexity", "PMD.CyclomaticComplexity", "checkstyle:CyclomaticComplexity", "checkstyle:ModifiedControlVariable"})
public final class NamedParametersParser {

    private static final char SINGLE_QUOTE = '\'';

    private final String originalSqlQuery;
    private final int queryLength;

    private boolean isInSingleQuotes;
    private boolean isInDoubleQuotes;
    private boolean isPartOfSingleLineComment;
    private boolean isPartOfMultiLineComment;
    private boolean isDoubleColon;
    private boolean isInSquareBrackets;
    private char currentCharacter;

    private NamedParametersParser(final String originalSqlQuery) {
        this.originalSqlQuery = Validators.notBlank(originalSqlQuery, "originalSqlQuery");
        this.queryLength = originalSqlQuery.length();
    }

    private String doParse() {
        final StringBuilder resultQuery = new StringBuilder(queryLength);
        for (int i = 0; i < queryLength; ++i) {
            currentCharacter = originalSqlQuery.charAt(i);
            if (isInSingleQuotes) {
                processNextSingleQuoteIfNeed();
            } else if (isInDoubleQuotes) {
                processNextDoubleQuoteIfNeed();
            } else if (isPartOfMultiLineComment) {
                processEndOfMultiLineCommentIfNeed(i);
            } else if (isDoubleColon) {
                isDoubleColon = false;
            } else if (isPartOfSingleLineComment) {
                processEndOfSingleLineCommentIfNeed();
            } else if (isInSquareBrackets) {
                processClosingSquareBracketIfNeed();
            } else {
                if (currentCharacter == SINGLE_QUOTE) {
                    isInSingleQuotes = true;
                } else if (currentCharacter == '"') {
                    isInDoubleQuotes = true;
                } else if (currentCharacter == '[') {
                    isInSquareBrackets = true;
                } else if (currentCharacter == '/' && nextChar(i) == '*') {
                    isPartOfMultiLineComment = true;
                } else if (currentCharacter == '-' && nextChar(i) == '-') {
                    isPartOfSingleLineComment = true;
                } else if (currentCharacter == ':' && hasNextChar(i) && nextChar(i) == ':') {
                    isDoubleColon = true;
                } else if (currentCharacter == ':' && hasNextChar(i) && Character.isJavaIdentifierStart(nextChar(i))) {
                    int j = i + 2;
                    while (j < queryLength && Character.isJavaIdentifierPart(originalSqlQuery.charAt(j))) {
                        ++j;
                    }
                    currentCharacter = '?'; // replace the parameter with a question mark
                    i = j - 1; // skip past the end if the parameter
                }
            }
            resultQuery.append(currentCharacter);
        }

        return resultQuery.toString();
    }

    private char nextChar(final int currentPosition) {
        return originalSqlQuery.charAt(currentPosition + 1);
    }

    private boolean hasNextChar(final int currentPosition) {
        return currentPosition + 1 < queryLength;
    }

    private void processNextSingleQuoteIfNeed() {
        if (currentCharacter == SINGLE_QUOTE) {
            isInSingleQuotes = false;
        }
    }

    private void processNextDoubleQuoteIfNeed() {
        if (currentCharacter == '"') {
            isInDoubleQuotes = false;
        }
    }

    private void processEndOfMultiLineCommentIfNeed(final int currentPosition) {
        if (currentCharacter == '*' && nextChar(currentPosition) == '/') {
            isPartOfMultiLineComment = false;
        }
    }

    private void processEndOfSingleLineCommentIfNeed() {
        if (currentCharacter == '\n') {
            isPartOfSingleLineComment = false;
        }
    }

    private void processClosingSquareBracketIfNeed() {
        if (currentCharacter == ']') {
            isInSquareBrackets = false;
        }
    }

    public static String parse(final String originalSqlQuery) {
        return new NamedParametersParser(originalSqlQuery).doParse();
    }
}
