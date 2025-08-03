/*
 * Copyright (c) 2019-2025. Ivan Vakhrushev and others.
 * https://github.com/mfvanek/pg-index-health
 *
 * This file is a part of "pg-index-health" - an embeddable schema linter for PostgreSQL
 * that detects common anti-patterns and promotes best practices.
 *
 * Licensed under the Apache License 2.0
 */

package io.github.mfvanek.pg.model.jackson;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class DbObjectSerializationTest {

    private static final String JSON_MODULE_PACKAGE = "io.github.mfvanek.pg.model.jackson";

    @Test
    void completenessTest() {
        try (ScanResult scan = new ClassGraph()
            .enableClassInfo()
            .acceptPackages("io.github.mfvanek.pg.model")
            .scan()) {

            scan.getClassesImplementing(DbObject.class.getName())
                .filter(classInfo -> !classInfo.isAbstract() && !classInfo.isInterface())
                .forEach(classInfo -> {
                    final String baseName = classInfo.getSimpleName();
                    final String packageName = classInfo.getPackageName();
                    final String packageSuffix = packageName.substring(packageName.lastIndexOf('.') + 1);
                    final String serializerClass = JSON_MODULE_PACKAGE + '.' + packageSuffix + '.' + baseName + "Serializer";
                    final String deserializerClass = JSON_MODULE_PACKAGE + '.' + packageSuffix + '.' + baseName + "Deserializer";

                    assertThatCode(() -> Class.forName(serializerClass))
                        .as("Serializer not found for " + classInfo.getName())
                        .doesNotThrowAnyException();
                    assertThatCode(() -> Class.forName(deserializerClass))
                        .as("Deserializer not found for " + classInfo.getName())
                        .doesNotThrowAnyException();
                });
        }
    }
}
