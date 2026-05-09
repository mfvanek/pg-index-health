# Rules for spring-boot-integration

Applies to: `spring-boot-integration/pg-index-health-test-starter/src/main/java/io/github/mfvanek/pg/spring/`

## Auto-configuration beans require a @Conditional annotation (AUTOCONFIGURED_BEANS_ARE_ANNOTATED_WITH_ANY_CONDITIONAL_ANNOTATION)

Every `@Bean` method declared inside a class annotated with `@AutoConfiguration` (or meta-annotated with it) must also carry `@ConditionalOnMissingBean`.

Reason: Spring must not create these beans if the application has already declared a bean of the same type. A missing `@Conditional` would silently override user-provided beans.

## Check beans must use concrete return types (CHECK_BEANS_SHOULD_USE_CONCRETE_TYPES_AS_RETURN_TYPES)

`@Bean` methods must not declare `AbstractCheckOnHost` or `DatabaseCheckOnHost` as their return type. Always return the concrete implementation type.

Reason: returning the abstract type prevents Spring from resolving the specific check implementation and breaks auto-wiring in downstream tests.
