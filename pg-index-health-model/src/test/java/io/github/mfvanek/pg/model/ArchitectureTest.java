/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model;

import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.github.mfvanek.pg.model.dbobject.DbObject;

import java.util.function.Predicate;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

@AnalyzeClasses(packages = "io.github.mfvanek.pg.model")
class ArchitectureTest {

    @ArchTest
    static final ArchRule ALL_PUBLIC_AND_NOT_ABSTRACT_CLASSES_SHOULD_BE_FINAL =
        classes().that().areNotInterfaces()
            .and().doNotHaveModifier(JavaModifier.ABSTRACT)
            .and().arePublic()
            .should().haveOnlyFinalFields()
            .andShould().haveOnlyPrivateConstructors()
            .andShould().haveModifier(JavaModifier.FINAL)
            .as("Public concrete classes must be final, immutable, and use static factory methods instead of public constructors");

    @ArchTest
    static final ArchRule ALL_CONCRETE_DB_OBJECTS_SHOULD_BE_COMPARABLE =
        classes().that().areAssignableTo(DbObject.class)
            .and().areNotInterfaces()
            .and().doNotHaveModifier(JavaModifier.ABSTRACT)
            .should().implement(Comparable.class)
            .as("Domain objects should implement Comparable for natural ordering");

    @ArchTest
    static final ArchRule AWARE_CLASSES_SHOULD_BE_INTERFACES =
        classes().that().haveSimpleNameEndingWith("Aware")
            .and().haveSimpleNameNotStartingWith("Abstract")
            .should().beInterfaces()
            .as("Classes named *Aware should be interfaces, not concrete classes");

    @ArchTest
    static final ArchRule PREDICATE_IMPLEMENTATIONS_SHOULD_RESIDE_IN_PREDICATES_PACKAGE =
        classes().that().areAssignableTo(Predicate.class)
            .and().areNotInterfaces()
            .should().resideInAPackage("..predicates..")
            .as("All Predicate implementations should be in the predicates package");
}
