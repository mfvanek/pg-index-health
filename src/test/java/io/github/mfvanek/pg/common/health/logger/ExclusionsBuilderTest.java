/*
 * Copyright (c) 2019-2020. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ExclusionsBuilderTest {

    @Test
    void toStringBuilderTest() {
        final ExclusionsBuilder builder = Exclusions.builder();
        assertNotNull(builder);
        assertEquals("ExclusionsBuilder{duplicatedIndexesExclusions='', intersectedIndexesExclusions='', " +
                        "unusedIndexesExclusions='', tablesWithMissingIndexesExclusions='', tablesWithoutPrimaryKeyExclusions='', " +
                        "indexesWithNullValuesExclusions='', indexSizeThresholdInBytes=0, tableSizeThresholdInBytes=0, " +
                        "indexBloatSizeThresholdInBytes=0, indexBloatPercentageThreshold=0, " +
                        "tableBloatSizeThresholdInBytes=0, tableBloatPercentageThreshold=0}",
                builder.toString());
    }
}
