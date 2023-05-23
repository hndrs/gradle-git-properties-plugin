import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.sonarqube").version("4.0.0.2929")
    `kotlin-dsl`
    `maven-publish`
    jacoco
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish").version("1.2.0")
    kotlin("jvm")
    idea
}

group = "io.hndrs.gradle"
version = "1.0.0"

java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

var publishingInfoPlugin: NamedDomainObjectProvider<PluginDeclaration>? = null

repositories {
    mavenCentral()
}

val tagList = listOf("git", "git properties", "gradle", "spring", "plugins", "kotlin", "configuration-cache")

gradlePlugin {
    website.set("https://github.com/hndrs/gradle-git-properties-plugin")
    vcsUrl.set("https://github.com/hndrs/gradle-git-properties-plugin.git")

    plugins {
        create("gitPropertiesPlugin") {
            id = "io.hndrs.git-properties"
            displayName = "Gradle git properties plugin"
            implementationClass = "io.hndrs.gradle.plugin.git.properties.GitPropertiesPlugin"
            description = "Generates Git properties and is compatible with gradles `configuration-cache`"
            tags.set(tagList)
        }
    }
}

dependencies {
    testImplementation(gradleTestKit())
    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("io.kotest:kotest-bom:5.6.2"))
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-assertions-core-jvm")
    testImplementation("io.mockk:mockk:1.13.5")
}

sonarqube {
    properties {
        property("sonar.projectKey", "hndrs_gradle-publishing-info-plugin")
        property("sonar.organization", "hndrs")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.exclusions", "**/sample/**")
    }
}

configure<JacocoPluginExtension> {
    toolVersion = "0.8.9"
}
tasks.withType<JacocoReport> {
    reports {
        xml.apply {
            this.required.set(true)
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
