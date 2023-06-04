plugins {
    id("java-library")
    id("maven-publish")
    id("pg-index-health.java-conventions")
}

group = "io.github.mfvanek"
version = "0.9.4"

//publishing {
//    publications {
//        create<MavenPublication>("library") {
//            from(components["java"])
//        }
//    }
//    repositories {
//        maven {
//            name = "myOrgPrivateRepo"
//            url = uri("build/my-repo")
//        }
//    }
//}
//
//// The project requires libraries to have a README containing sections configured below
//val readmeCheck by tasks.registering(com.example.ReadmeVerificationTask::class) {
//    readme.set(layout.projectDirectory.file("README.md"))
//    readmePatterns.set(listOf("^## API$", "^## Changelog$"))
//}
//
//tasks.named("check") { dependsOn(readmeCheck) }
