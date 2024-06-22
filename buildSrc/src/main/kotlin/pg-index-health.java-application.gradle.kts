plugins {
    id("pg-index-health.java-compilation")
    id("pg-index-health.java-conventions")
    id("pg-index-health.forbidden-apis")
}

private val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    versionCatalog.findLibrary("slf4j-simple").ifPresent {
        spotbugsSlf4j(it)
    }
}
