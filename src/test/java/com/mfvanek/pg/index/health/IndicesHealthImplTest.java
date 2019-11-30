/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class IndicesHealthImplTest {

    @Test
    void getInvalidIndicesOnEmptyDatabase() {
        fail();
    }

    @Test
    void getDuplicatedIndicesOnEmptyDatabase() {
        fail();
    }

    @Test
    void getIntersectedIndicesOnEmptyDatabase() {
        fail();
    }

    @Test
    void getUnusedIndicesOnEmptyDatabase() {
        fail();
    }

    @Test
    void getForeignKeysNotCoveredWithIndexOnEmptyDatabase() {
        fail();
    }

    @Test
    void getTablesWithMissingIndicesOnEmptyDatabase() {
        fail();
    }

    @Test
    void getTablesWithoutPrimaryKeyOnEmptyDatabase() {
        fail();
    }

    @Test
    void getIndicesWithNullValuesOnEmptyDatabase() {
        fail();
    }
}
