plugins {
    id("pg-index-health.java-library")
    id("pg-index-health.pitest")
}

description = "pg-index-health-model-jackson-module is an extension for serializing and deserializing database objects to and from JSON format."

dependencies {
    api(project(":pg-index-health-model"))
    implementation(platform(libs.jackson.bom))
    implementation("com.fasterxml.jackson.core:jackson-databind")

    testImplementation("io.github.classgraph:classgraph:4.8.181")
}

val generateModuleVersion = tasks.register<GenerateModuleVersionTask>("generateModuleVersion") {
    moduleGroup.set(project.group.toString())
    moduleName.set(project.name)
    moduleVersion.set(project.version.toString())
    outputDir.set(layout.buildDirectory.dir("generated/sources/version"))
}

sourceSets["main"].java.srcDir(generateModuleVersion.map { it.outputs.files })

tasks.named("compileJava") {
    dependsOn(generateModuleVersion)
}
