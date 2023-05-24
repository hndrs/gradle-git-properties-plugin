import io.hndrs.gradle.plugin.git.properties.GenerateGitPropertiesTask

plugins {
    id("io.hndrs.git-properties")
    `java`
}

tasks.withType(GenerateGitPropertiesTask::class.java) {
    dotGitDirectory.set(File("../.git"))
    output.set(File("build/custom/directory/git.properties"))
}