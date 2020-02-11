package io.github.mfvanek.pg.model;

/**
 * Allows to get information about bloat in database.
 *
 * @author Ivan Vakhrushev
 */
public interface BloatAware {

    /**
     * Gets bloat amount in bytes.
     *
     * @return bloat amount
     */
    long getBloatSizeInBytes();

    /**
     * Gets bloat percentage.
     *
     * @return bloat percentage
     */
    int getBloatPercentage();
}
