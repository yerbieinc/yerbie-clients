# Publishing to Maven Locally
The command `./gradlew publishToMavenLocal` will publish the build's pom and artifacts to `~/.m2`. From there, you can then use maven local in other projects to pull yerbie artifacts locally.

# Publishing to Github Packages
Tag the commit to be published with `java/vn.n.n`, for example `git tag java/v0.0.1`. Push the tag and CI will automatically publish the package.

# Publishing to Maven Central
Run `./gradlew publishMavenJavaPublicationToMavenCentralRepository`. This will push to the snapshot repo if the version ends with `-SNAPSHOT`, otherwise the release repo.
If publishing to the central repository, follow the steps [here](https://central.sonatype.org/publish/release/), to release.
