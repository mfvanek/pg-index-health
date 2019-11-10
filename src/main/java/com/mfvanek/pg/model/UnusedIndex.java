package com.mfvanek.pg.model;

public class UnusedIndex extends Index {

    private final long indexSizeInBytes;
    private final long indexScans;

    public UnusedIndex(String tableName, String indexName, long indexSizeInBytes, long indexScans) {
        super(tableName, indexName);
        this.indexSizeInBytes = indexSizeInBytes;
        this.indexScans = indexScans;
    }

    @Override
    public String toString() {
        return UnusedIndex.class.getSimpleName() + "{" +
                innerToString() +
                ", indexSizeInBytes=" + indexSizeInBytes +
                ", indexScans=" + indexScans +
                "}";
    }
}
