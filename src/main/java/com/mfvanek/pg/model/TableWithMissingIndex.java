package com.mfvanek.pg.model;

import javax.annotation.Nonnull;
import java.util.Objects;

// В нормальной ситуации при доступе к таблице в основном должны использоваться индексы.
// Если индексов нет или их мало, то seqScans будет больше, чем indexScans
public class TableWithMissingIndex implements TableAware {

    private final String tableName;
    private final long seqScans;
    private final long indexScans;

    public TableWithMissingIndex(@Nonnull String tableName, long seqScans, long indexScans) {
        this.tableName = Validators.tableNameNotBlank(tableName);
        this.seqScans = seqScans;
        this.indexScans = indexScans;
    }

    @Override
    @Nonnull
    public String getTableName() {
        return tableName;
    }

    public long getSeqScans() {
        return seqScans;
    }

    public long getIndexScans() {
        return indexScans;
    }

    @Override
    public String toString() {
        return TableWithMissingIndex.class.getSimpleName() + "{" +
                "tableName=\'" + tableName + "\'" +
                ", seqScans=" + seqScans +
                ", indexScans=" + indexScans +
                "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TableWithMissingIndex that = (TableWithMissingIndex) o;
        return tableName.equals(that.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName);
    }
}
