/*
 * Copyright (c) 2019. Ivan Vakhrushev. All rights reserved.
 * https://github.com/mfvanek
 */

package com.mfvanek.pg.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetExtractor<T> {

    T extractData(ResultSet rs) throws SQLException;
}
