/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health.checks.cluster;

import io.github.mfvanek.pg.connection.HighAvailabilityPgConnection;
import io.github.mfvanek.pg.core.checks.host.ObjectsWithUpperCaseNamesCheckOnHost;
import io.github.mfvanek.pg.model.dbobject.AnyObject;

/**
 * Check for objects whose names contain uppercase letters on all hosts in the cluster.
 * <p>
 * Prefer names_like_this over NamesLikeThis.
 * PostgreSQL folds unquoted identifiers to lowercase, so using uppercase forces you to always quote the identifier.
 *
 * @author Ivan Vakhrushev
 * @see <a href="https://wiki.postgresql.org/wiki/Don%27t_Do_This#Don.27t_use_upper_case_table_or_column_names">PostgreSQL Wiki: Don't use upper case table or column names</a>
 * @since 0.41.0
 */
public class ObjectsWithUpperCaseNamesCheckOnCluster extends AbstractCheckOnCluster<AnyObject> {

    /**
     * Constructs a new instance of {@code ObjectsWithUpperCaseNamesCheckOnCluster}.
     *
     * @param haPgConnection the high-availability connection to the PostgreSQL cluster; must not be null
     */
    public ObjectsWithUpperCaseNamesCheckOnCluster(final HighAvailabilityPgConnection haPgConnection) {
        super(haPgConnection, ObjectsWithUpperCaseNamesCheckOnHost::new);
    }
}
