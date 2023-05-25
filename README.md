[![Maven metadata URL](https://img.shields.io/maven-metadata/v?color=green&label=GRADLE%20PLUGIN&metadataUrl=https%3A%2F%2Fplugins.gradle.org%2Fm2%2Fio%2Fhndrs%2Fgit-properties%2Fio.hndrs.git-properties.gradle.plugin%2Fmaven-metadata.xml&style=for-the-badge)](https://plugins.gradle.org/plugin/io.hndrs.git-properties)
[![Coverage](https://img.shields.io/sonar/coverage/hndrs_gradle-git-properties-plugin?server=https%3A%2F%2Fsonarcloud.io&style=for-the-badge)](https://sonarcloud.io/dashboard?id=hndrs_gradle-git-properties-plugin)
[![Supported Java Version](https://img.shields.io/badge/Supported%20Java%20Version-17%2B-informational?style=for-the-badge)]()
[![Sponsor](https://img.shields.io/static/v1?logo=GitHub&label=Sponsor&message=%E2%9D%A4&color=ff69b4&style=for-the-badge)](https://github.com/sponsors/marvinschramm)

# Gradle Git Properties

This is a simple gradle plugin that can generate git properties via a `generateGitProperties` task.
If you ask yourself "Why? there is already plugins out there that do this.", you are right.

This plugin was build specifically with the purpose of supporting
gradles [configuration-cache](https://docs.gradle.org/8.1.1/userguide/configuration_cache.html) feature.
If you don`t need this functionality, you might choose a more mature plugin.

## Using the Plugin

Add the following dependency to your plugin block

```kotlin
plugins {
    id("io.hndrs.git-properties") version "1.0.0"
}
```

The Plugin registers a ```generateGitProperties``` task to the project.

#### GenerateProperties Task

If the project includes the Gradle-Java-Plugin the `generateGitProperties` task will be attached to the
`classes`

#### Default Configuration

When the task is not configured

- it is assumed that a `.git` folder is present in the root of the project.
- the build will fail when an error occurs during the properties generation unless the `--continue-on-error` option is
  provided
- the output file is `resources/main/git.properties`

The task can be configured the following way

```kotlin
import io.hndrs.gradle.plugin.git.properties.GenerateGitPropertiesTask

tasks.withType(GenerateGitPropertiesTask::class.java) {
    dotGitDirectory.set(File(".git"))
    continueOnError.set(false)
    output.set(File("resources/main/git.properties"))
}
```

> File (build.gradle.kts)

#### Available Properties

```properties
git.branch=main
git.build.host=localHost
git.build.user.email=john.smith@gradlemail.com
git.build.user.name=John Smith
git.commit.id=e2f8a7bb72036e7a7a03ba243ca0414914cdfa82
git.commit.id.abbrev=e2f8a7b
git.commit.message.full=Merge pull request #1 from hndrs/prepare-initial-release\n\nPreparation of first release. Covers important cache test cases and cleans up code
git.commit.message.short=Merge pull request #1 from hndrs/prepare-initial-release
git.commit.signed=true
git.commit.time=2023-05-24T21:42:38Z
git.commit.user.email=john.smith@gradlemail.com
git.commit.user.name=John Smith
git.remote.origin.url=git@github.com:hndrs/gradle-git-properties-plugin.git
```