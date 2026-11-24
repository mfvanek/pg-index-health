# Rules for pg-index-health-model

Applies to: `pg-index-health-model/src/main/java/io/github/mfvanek/pg/model/`

## Immutability and construction (ALL_PUBLIC_AND_NOT_ABSTRACT_CLASSES_SHOULD_BE_FINAL)

Every public, non-abstract, non-interface class must be:
- Declared `final`
- Have only `private` constructors — use static factory methods (`of()`, `ofType()`, `ofNotNull()`, etc.)
- Have only `final` fields

Never add a public constructor or a non-final field to a concrete class in this module.

## Domain objects must be Comparable (ALL_CONCRETE_DB_OBJECTS_SHOULD_BE_COMPARABLE)

Every concrete class that implements `DbObject` (directly or through an abstract base) must also implement `Comparable<T>`.

Ordering convention:
- Compare by `tableName` first (where applicable), then by the primary identifier field
- Use local variables when calling `getTableName()` on both sides — it involves a list access (`list.get(0)`) and would otherwise run twice
- Start `compareTo` with `Objects.requireNonNull(other, "other cannot be null")`

## Identity semantics (EQUALS_HASHCODE_COMPARETO_USE_ONLY_IDENTITY_FIELDS)

Model objects mirror PostgreSQL catalog objects. `equals`, `hashCode` and `compareTo` must use only
the fields that PostgreSQL itself uses to identify the object uniquely — never descriptive attributes.

- Table: `tableName` (qualified name is unique).
- Column: `tableName` + `columnName` (`pg_attribute` is unique on `(attrelid, attname)`, so a table
  cannot have two columns with the same name). Nullability (`notNull`) and type (`columnType`) are
  attributes and must not participate.
- Index / sequence: `tableName` + `indexName` / `sequenceName` (relation names are unique in a schema).
- Constraint: `tableName` + `constraintName` (`pg_constraint` is unique on `(conrelid, conname)`).
- Stored function: `functionName` + `functionSignature` (overloads differ by argument types).
- Wrapper objects (`ForeignKey`, `IndexWithColumns`, `TableWithColumns`, `ColumnWithSerialType`, etc.)
  delegate to the wrapped identity object only; sizes, columns, sequence names and other payload are ignored.

`compareTo` must be consistent with `equals`: compare the same fields in the same order and return 0
exactly when `equals` returns true. `hashCode` must use exactly the fields used by `equals`.
In tests, `EqualsVerifier` must list every non-identity field in `withIgnoredFields(...)`;
attribute values are verified by `usingRecursiveFieldByFieldElementComparator()` in check tests.

## Capability interfaces naming (AWARE_CLASSES_SHOULD_BE_INTERFACES)

Any type whose simple name ends with `Aware` must be an interface. Never create a concrete or abstract class with an `Aware` suffix.

## Predicate placement (PREDICATE_IMPLEMENTATIONS_SHOULD_RESIDE_IN_PREDICATES_PACKAGE)

All classes that implement `Predicate<DbObject>` — directly or through inheritance — must reside in the `predicates` sub-package. Do not add filtering logic to domain or utility packages.

## Columns are ordered (COLUMNS_PARAMETERS_MUST_BE_LISTS)

The order of columns in `ForeignKey`, `IndexWithColumns` and `TableWithColumns` (and any other `ColumnsAware` class) is significant:
FK columns are matched with the referenced columns by position, and indexes on `(a, b)` and `(b, a)` are different.

- Factories and constructors take columns as `List<Column>` or as varargs `(Column column, Column... otherColumns)`.
  Never widen these parameters to `Collection<Column>`/`Iterable<Column>`: a `HashSet` would silently produce a random column order.
- `SequencedCollection` is not an option while the project targets Java 17.
- Keep the class-level Javadoc note explaining why columns are a `List`.
