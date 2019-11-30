/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.index.maintenance;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
interface ResultSetExtractor<T> {

    T extractData(ResultSet rs) throws SQLException;
}
