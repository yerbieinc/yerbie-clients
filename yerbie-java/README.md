# Publishing to Maven Locally
The command `./gradlew publishToMavenLocal` will publish the build's pom and artifacts to `~/.m2`. From there, you can then use maven local in other projects to pull yerbie artifacts locally.

# Publishing to Github Packages
Tag the commit to be published with `java/vn.n.n`, for example `git tag java/v0.0.1`. Push the tag and CI will automatically publish the package.


