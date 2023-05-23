rootProject.name = "gradle-git-properties-plugin"

pluginManagement {
    val kotlinVersion = "1.8.21"
    plugins {
        kotlin("jvm").version(kotlinVersion)
        kotlin("kapt").version(kotlinVersion)
        id("maven-publish")
        id("idea")
    }
    repositories {
        
    }
}