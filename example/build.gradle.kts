import io.hndrs.gradle.plugin.git.properties.GenerateGitPropertiesTask

plugins {
    id("io.hndrs.git-properties")
}

tasks.withType(GenerateGitPropertiesTask::class.java) {
    dotGitDirectory.set(File("../.git"))
}