plugins {
    id("pg-index-health.java-library")
    id("pg-index-health.pitest")
}

description = "pg-index-health-model-jackson2-module is an extension for serializing and deserializing database objects to and from JSON format with Jackson 2."

dependencies {
    api(project(":pg-index-health-model"))
    implementation(platform(libs.jackson2.bom))
    implementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation(libs.classgraph)
}

val generateModuleVersion = tasks.register<GenerateJackson2ModuleVersionTask>("generateJackson2ModuleVersion") {
    moduleGroup.set(project.group.toString())
    moduleName.set(project.name)
    moduleVersion.set(project.version.toString())
    outputDir.set(layout.buildDirectory.dir("generated/sources/version"))
}

sourceSets["main"].java.srcDir(generateModuleVersion.map { it.outputs.files })

tasks {
    named("compileJava") {
        dependsOn(generateModuleVersion)
    }

    jar {
        manifest {
            attributes("Automatic-Module-Name" to "io.github.mfvanek.pg.model.jackson2")
        }
    }
}
