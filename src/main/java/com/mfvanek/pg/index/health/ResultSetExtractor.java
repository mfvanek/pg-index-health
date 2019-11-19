/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.health;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
interface ResultSetExtractor {

    void extractData(ResultSet rs) throws SQLException;
}
