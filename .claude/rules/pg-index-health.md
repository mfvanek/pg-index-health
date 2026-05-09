# Rules for pg-index-health

Applies to: `pg-index-health/src/main/java/io/github/mfvanek/pg/health/`

## Cluster-level check placement (CHECK_ON_CLUSTER_CLASSES_SHOULD_RESIDE_IN_CORRECT_PACKAGE)

Every concrete (non-abstract, non-interface) class that extends `AbstractCheckOnCluster` must reside in the `checks.cluster` sub-package.

Do not place cluster-level check implementations in any other package within this module.
