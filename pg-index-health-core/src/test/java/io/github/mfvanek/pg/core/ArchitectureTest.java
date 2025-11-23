/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.core;

import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.github.mfvanek.pg.core.checks.common.ResultSetExtractor;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "io.github.mfvanek.pg.core")
class ArchitectureTest {

    @ArchTest
    static final ArchRule ALL_EXTRACTORS_SHOULD_BE_FINAL =
        classes().that().areNotInterfaces().and().implement(ResultSetExtractor.class)
            .should().haveOnlyFinalFields()
            .andShould().haveOnlyPrivateConstructors()
            .andShould().haveModifier(JavaModifier.FINAL);
}
