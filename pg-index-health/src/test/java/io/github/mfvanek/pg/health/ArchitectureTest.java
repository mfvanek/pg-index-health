/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.health;

import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.github.mfvanek.pg.health.checks.cluster.AbstractCheckOnCluster;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "io.github.mfvanek.pg.health")
class ArchitectureTest {

    @ArchTest
    static final ArchRule CHECK_ON_CLUSTER_CLASSES_SHOULD_RESIDE_IN_CORRECT_PACKAGE =
        classes().that().areNotInterfaces()
            .and().doNotHaveModifier(JavaModifier.ABSTRACT)
            .and().areAssignableTo(AbstractCheckOnCluster.class)
            .should().resideInAPackage("..checks.cluster")
            .as("All concrete cluster-level check classes should reside in the checks.cluster package");
}
