/*
 * Copyright (c) 2019-2022. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - a Java library for
 * analyzing and maintaining indexes health in PostgreSQL databases.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.common.health.logger;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExclusionsBuilderTest {

    @Test
    void toStringBuilderTest() {
        assertThat(Exclusions.builder())
                .hasToString("ExclusionsBuilder{duplicatedIndexesExclusions='', intersectedIndexesExclusions='', " +
                        "unusedIndexesExclusions='', tablesWithMissingIndexesExclusions='', tablesWithoutPrimaryKeyExclusions='', " +
                        "indexesWithNullValuesExclusions='', indexSizeThresholdInBytes=0, tableSizeThresholdInBytes=0, " + "indexBloatSizeThresholdInBytes=0, indexBloatPercentageThreshold=0, " +
                        "tableBloatSizeThresholdInBytes=0, tableBloatPercentageThreshold=0}");
    }
}
