package io.github.mfvanek.pg.model;

import io.github.mfvanek.pg.utils.Validators;

import javax.annotation.Nonnull;

/**
 * Represents database index with information about bloat.
 *
 * @author Ivan Vakhrushev
 */
public class IndexWithBloat extends IndexWithSize implements BloatAware {

    private long bloatSizeInBytes;
    private int bloatPercentage;

    private IndexWithBloat(@Nonnull String tableName,
                           @Nonnull String indexName,
                           long indexSizeInBytes,
                           long bloatSizeInBytes,
                           int bloatPercentage) {
        super(tableName, indexName, indexSizeInBytes);
        this.bloatSizeInBytes = Validators.sizeNotNegative(bloatSizeInBytes, "bloatSizeInBytes");
        this.bloatPercentage = Validators.argumentNotNegative(bloatPercentage, "bloatPercentage");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getBloatSizeInBytes() {
        return bloatSizeInBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBloatPercentage() {
        return bloatPercentage;
    }

    @Override
    protected String innerToString() {
        return super.innerToString() + ", bloatSizeInBytes=" + bloatSizeInBytes +
                ", bloatPercentage=" + bloatPercentage;
    }

    @Override
    public String toString() {
        return IndexWithBloat.class.getSimpleName() + '{' + innerToString() + '}';
    }

    @Nonnull
    public static IndexWithBloat of(@Nonnull String tableName,
                                    @Nonnull String indexName,
                                    long indexSizeInBytes,
                                    long bloatSizeInBytes,
                                    int bloatPercentage) {
        return new IndexWithBloat(tableName, indexName, indexSizeInBytes, bloatSizeInBytes, bloatPercentage);
    }
}
