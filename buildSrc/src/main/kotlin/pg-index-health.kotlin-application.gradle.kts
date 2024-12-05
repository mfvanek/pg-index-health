import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.spring")
    id("pg-index-health.java-compilation")
    id("io.gitlab.arturbosch.detekt")
    id("pg-index-health.forbidden-apis")
}

private val versionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    versionCatalog.findLibrary("detekt-formatting").ifPresent {
        detektPlugins(it)
    }
    versionCatalog.findLibrary("detekt-libraries").ifPresent {
        detektPlugins(it)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "11"
    }
}

detekt {
    toolVersion = versionCatalog.findVersion("detekt").get().requiredVersion
    config.setFrom(file("${rootDir}/config/detekt/detekt.yml"))
    buildUponDefaultConfig = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
