plugins {
    id("pg-index-health.java-compilation")
    id("pg-index-health.java-conventions")
    alias(libs.plugins.spring.boot.gradlePlugin)
    alias(libs.plugins.spring.dependency.management)
}

ext["commons-lang3.version"] = libs.versions.commons.lang3.get()
ext["assertj.version"] = libs.versions.assertj.get()

dependencies {
    implementation(project(":spring-boot-integration:pg-index-health-test-starter"))
    implementation(libs.spring.boot.starter.root)

    testImplementation(libs.spring.boot.starter.test)

    spotbugsSlf4j(libs.slf4j.simple)
}

checkstyle {
    configFile = file("../../config/checkstyle/checkstyle.xml")
}

pmd {
    ruleSetFiles = files("../../config/pmd/pmd.xml")
}

spotbugs {
    excludeFilter.set(file("../../config/spotbugs/exclude.xml"))
}
