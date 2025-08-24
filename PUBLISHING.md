# Publishing Guide

At the current stage we publish new versions of the project from a local machine and raise the version manually.

## Before Publishing

Make sure that root `build.gradle.kts` contains the correct version.
We use [Semantic Versioning](https://semver.org/) and raise the version accordingly.

Pay special attention to the presence of backward incompatible (breaking) changes in the release.

Try to prepare a short changelog and highlight the main changes in the upcoming release.

## Build and Test locally

Make sure that the `master` branch is up to date and builds successfully on your local machine.

```shell
./gradlew clean
```

```shell
./gradlew build
```

### Prepare the root `gradle.properties`

Find [the root gradle.properties](https://docs.gradle.org/current/userguide/build_environment.html) file on your local machine.

You need to add the following properties:

```properties
signing.gnupg.executable=gpg
signing.gnupg.useLegacyGpg=false
signing.gnupg.keyName=<key name>
signing.gnupg.passphrase=<key passphrase>
systemProp.org.gradle.internal.publish.checksums.insecure=true
mavenCentralUsername=<sonatype username>
mavenCentralPassword=<sonatype password>
```

## Publish to Maven Local

Make sure that the artifacts are successfully signed and published to the local repository:

```shell
./gradlew publishToMavenLocal
```

## Publish to Maven Central

Publish the artifacts to the [Central Portal](https://central.sonatype.com/publishing/namespaces):

```shell
./gradlew publishToMavenCentral
```

Then go to the [Central Portal](https://central.sonatype.com/publishing/namespaces)
and manually release the artifacts or use a Gradle task with automatic release:

```shell
./gradlew publishAndReleaseToMavenCentral
```

## After Publishing

Wait for the artifacts to be publicly available.
Track their appearance in [Maven repo](https://repo.maven.apache.org/maven2/io/github/mfvanek/pg-index-health/).

Then manually create a [new release on GitHub](https://github.com/mfvanek/pg-index-health/releases).

After that create a new pull request to update the `README.md` file with the latest published version.
Also update the root `build.gradle.kts` and raise the version for the next development iteration.
