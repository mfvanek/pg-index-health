/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.connection;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

class PgUrlParserTest {

    @Test
    void extractNamesAndUrlsForEachHost() {
        var extractResult = PgUrlParser.extractNamesAndUrlsForEachHost(
                "jdbc:postgresql://host-1:6432,host-2:6432,host-3:6432,host-4:6432/db_name?ssl=true&sslmode=require");
        assertThat(extractResult, hasSize(4));
        assertThat(extractResult, containsInAnyOrder(
                Pair.of("host-1:6432", "jdbc:postgresql://host-1:6432/db_name?ssl=true&sslmode=require"),
                Pair.of("host-2:6432", "jdbc:postgresql://host-2:6432/db_name?ssl=true&sslmode=require"),
                Pair.of("host-3:6432", "jdbc:postgresql://host-3:6432/db_name?ssl=true&sslmode=require"),
                Pair.of("host-4:6432", "jdbc:postgresql://host-4:6432/db_name?ssl=true&sslmode=require")
        ));
    }
}
