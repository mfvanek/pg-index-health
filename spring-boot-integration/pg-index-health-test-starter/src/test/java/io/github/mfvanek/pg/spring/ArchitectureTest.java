/*
 * Copyright (c) 2019-2026. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.spring;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.host.AbstractCheckOnHost;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;

@AnalyzeClasses(packages = "io.github.mfvanek.pg.spring")
class ArchitectureTest {

    @ArchTest
    static final ArchRule AUTOCONFIGURED_BEANS_ARE_ANNOTATED_WITH_ANY_CONDITIONAL_ANNOTATION =
        methods().that().areAnnotatedWith(Bean.class)
            .and().areDeclaredInClassesThat().areMetaAnnotatedWith(AutoConfiguration.class)
            .should().beAnnotatedWith(ConditionalOnMissingBean.class)
            .as("Auto-configuration beans should be annotated with any @Conditional annotation")
            .because("Spring Framework should not create those beans if an application already declared beans of the same type");

    @ArchTest
    static final ArchRule CHECK_BEANS_SHOULD_USE_CONCRETE_TYPES_AS_RETURN_TYPES =
        methods().that().areAnnotatedWith(Bean.class)
            .should().notHaveRawReturnType(AbstractCheckOnHost.class)
            .andShould().notHaveRawReturnType(DatabaseCheckOnHost.class);
}
