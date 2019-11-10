package com.mfvanek.pg.model;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Objects;

final class Validators {

    private Validators() {
        throw new UnsupportedOperationException();
    }

    static String tableNameNotBlank(@Nonnull String tableName) {
        if (StringUtils.isBlank(Objects.requireNonNull(tableName, "tableName cannot be null"))) {
            throw new IllegalArgumentException("tableName");
        }
        return tableName;
    }

    static String indexNameNotBlank(@Nonnull String indexName) {
        if (StringUtils.isBlank(Objects.requireNonNull(indexName, "indexName cannot be null"))) {
            throw new IllegalArgumentException("indexName");
        }
        return indexName;
    }
}
