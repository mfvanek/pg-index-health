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

## Capability interfaces naming (AWARE_CLASSES_SHOULD_BE_INTERFACES)

Any type whose simple name ends with `Aware` must be an interface. Never create a concrete or abstract class with an `Aware` suffix.

## Predicate placement (PREDICATE_IMPLEMENTATIONS_SHOULD_RESIDE_IN_PREDICATES_PACKAGE)

All classes that implement `Predicate<DbObject>` — directly or through inheritance — must reside in the `predicates` sub-package. Do not add filtering logic to domain or utility packages.
