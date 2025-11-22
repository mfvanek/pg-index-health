## Standard test

Add a standard test to your project as shown below. Ideally, all checks should pass and return an empty result.

```java
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.predicates.SkipByColumnNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipFlywayTablesPredicate;
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate;
import io.github.mfvanek.pg.model.predicates.SkipLiquibaseTablesPredicate;
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseStructureStaticAnalysisTest {

    @Autowired
    private List<DatabaseCheckOnHost<? extends @NonNull DbObject>> checks;

    @Test
    void checksShouldWork() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.values());

        // Predicates allow you to customize the list of permanent exceptions for your project.
        // Just filter out the database objects that you definitely don't want to see in the check results.
        // Do not use predicates for temporary exceptions or recording deviations in the database structure.
        final Predicate<DbObject> exclusions = SkipLiquibaseTablesPredicate.ofDefault()
            .and(SkipFlywayTablesPredicate.ofDefault()) // if you use Flyway
            .and(SkipTablesByNamePredicate.ofName("my_awesome_table"))
            .and(SkipIndexesByNamePredicate.ofName("my_awesome_index"))
            .and(SkipByColumnNamePredicate.ofName("id"));

        checks.stream()
            .filter(DatabaseCheckOnHost::isStatic)
            .forEach(c ->
                assertThat(c.check(exclusions))
                    .as(c.getName())
                    .isEmpty());
    }
}

```

### Standard test in Kotlin

```kotlin
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost
import io.github.mfvanek.pg.core.checks.common.Diagnostic
import io.github.mfvanek.pg.model.dbobject.DbObject
import io.github.mfvanek.pg.model.predicates.SkipByColumnNamePredicate
import io.github.mfvanek.pg.model.predicates.SkipFlywayTablesPredicate
import io.github.mfvanek.pg.model.predicates.SkipIndexesByNamePredicate
import io.github.mfvanek.pg.model.predicates.SkipLiquibaseTablesPredicate
import io.github.mfvanek.pg.model.predicates.SkipTablesByNamePredicate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
internal class DatabaseStructureStaticAnalysisTest {

    @Autowired
    private lateinit var checks: List<DatabaseCheckOnHost<out DbObject>>

    @Test
    fun checksShouldWork() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.entries.toTypedArray())

        // Predicates allow you to customize the list of permanent exceptions for your project.
        // Just filter out the database objects that you definitely don't want to see in the check results.
        // Do not use predicates for temporary exceptions or recording deviations in the database structure.
        val exclusions = SkipLiquibaseTablesPredicate.ofDefault()
            .and(SkipFlywayTablesPredicate.ofDefault()) // if you use Flyway
            .and(SkipTablesByNamePredicate.ofName("my_awesome_table"))
            .and(SkipIndexesByNamePredicate.ofName("my_awesome_index"))
            .and(SkipByColumnNamePredicate.ofName("id"))

        checks
            .filter { it.isStatic }
            .forEach {
                assertThat(it.check(exclusions))
                    .`as`(it.name)
                    .isEmpty()
            }
    }
}
```

### Recording deviations in the database structure

All deviations in the database structure found during checks can be recorded in the code and fixed later.

```java
import io.github.mfvanek.pg.core.checks.common.DatabaseCheckOnHost;
import io.github.mfvanek.pg.core.checks.common.Diagnostic;
import io.github.mfvanek.pg.model.column.Column;
import io.github.mfvanek.pg.model.column.ColumnWithSerialType;
import io.github.mfvanek.pg.model.context.PgContext;
import io.github.mfvanek.pg.model.dbobject.DbObject;
import io.github.mfvanek.pg.model.table.Table;
import org.assertj.core.api.ListAssert;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@SpringBootTest
@ActiveProfiles("test")
class DatabaseStructureStaticAnalysisTest {

    @Autowired
    private List<DatabaseCheckOnHost<? extends @NonNull DbObject>> checks;

    @Test
    void checksShouldWorkForAdditionalSchema() {
        assertThat(checks)
            .hasSameSizeAs(Diagnostic.values());

        final PgContext ctx = PgContext.of("additional_schema");
        checks.stream()
            .filter(DatabaseCheckOnHost::isStatic)
            .forEach(c -> {
                final ListAssert<? extends DbObject> listAssert = assertThat(c.check(ctx))
                    .as(c.getName());

                switch (c.getName()) {
                    case "TABLES_WITHOUT_DESCRIPTION", "TABLES_NOT_LINKED_TO_OTHERS" -> listAssert
                        .hasSize(1)
                        .asInstanceOf(list(Table.class))
                        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("tableSizeInBytes")
                        .containsExactly(Table.of(ctx, "additional_table"))
                        .allMatch(t -> t.getTableSizeInBytes() > 1L);

                    case "COLUMNS_WITHOUT_DESCRIPTION" -> listAssert
                        .hasSize(2)
                        .asInstanceOf(list(Column.class))
                        .usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(
                            Column.ofNotNull(ctx, "additional_table", "id"),
                            Column.ofNotNull(ctx, "additional_table", "name")
                        );

                    case "PRIMARY_KEYS_WITH_SERIAL_TYPES" -> listAssert
                        .hasSize(1)
                        .asInstanceOf(list(ColumnWithSerialType.class))
                        .usingRecursiveFieldByFieldElementComparator()
                        .containsExactly(
                            ColumnWithSerialType.ofBigSerial(ctx, Column.ofNotNull(ctx, "additional_table", "id"), "additional_table_id_seq")
                        );

                    default -> listAssert.isEmpty();
                }
            });
    }
}
```
