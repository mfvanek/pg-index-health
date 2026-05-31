# Rules for Javadoc

Applies to: all Java source files in `src/main/java/`

## @since tag must reflect the current project version (JAVADOC_SINCE_MUST_MATCH_PROJECT_VERSION)

Every new public class and every new public method added to an existing class must carry a `@since` Javadoc tag.

**Determining the version:**

1. Open the root `build.gradle.kts` file.
2. Read the value of the `version` property (e.g., `version = "0.41.1"`).
3. Use that exact string as the `@since` value.

Example — if `build.gradle.kts` contains `version = "0.41.1"`:

```java
/**
 * Check for tables with incrementing column names on a specific host.
 *
 * @author Ivan Vakhrushev
 * @since 0.41.1
 */
public class TablesWithIncrementingColumnsCheckOnHost extends AbstractCheckOnHost<TableWithColumns> {
```

Do **not** hardcode a version from memory or a previous session. Always read `build.gradle.kts` at the time of writing to get the current value.
