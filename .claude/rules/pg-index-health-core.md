# Rules for pg-index-health-core

Applies to: `pg-index-health-core/src/main/java/io/github/mfvanek/pg/core/`

## Extractor immutability (ALL_EXTRACTORS_SHOULD_BE_FINAL)

Every class that implements `ResultSetExtractor` must be:
- Declared `final`
- Have only `private` constructors
- Have only `final` fields

Extractors are stateless converters; they must not be subclassed or hold mutable state.

## Host-level check placement (CHECK_ON_HOST_CLASSES_SHOULD_RESIDE_IN_CORRECT_PACKAGE)

Every concrete (non-abstract, non-interface) class that extends `AbstractCheckOnHost` must reside in the `checks.host` sub-package.

Do not place host-level check implementations in any other package within this module.
