import io.hndrs.gradle.plugin.Developer
import io.hndrs.gradle.plugin.License
import io.hndrs.gradle.plugin.Organization
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.sonarqube").version("4.0.0.2929")
    `kotlin-dsl`
    `maven-publish`
    jacoco
    signing
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish").version("1.2.0")
    kotlin("jvm")
    idea
    id("io.hndrs.publishing-info").version("3.1.0")
}

group = "io.hndrs.gradle"
version = "1.0.0"

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

val signingKey: String? by project
val signingPassword: String? by project
if (signingKey != null && signingPassword != null) {
    signing {
        useInMemoryPgpKeys(groovy.json.StringEscapeUtils.unescapeJava(signingKey), signingPassword)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

publishingInfo {
    name = "Gradle git properties plugin"
    description = "Simple Gradle Git Properties Plugin with gradle configuration-cache support"
    inceptionYear = "2023"
    url = "https://github.com/hndrs/gradle-git-properties-plugin"
    license = License(
        "https://github.com/hndrs/gradle-git-properties-plugin/blob/main/LICENSE",
        "MIT License"
    )
    developers = listOf(
        Developer("marvinschramm", "Marvin Schramm", "marvin.schramm@gmail.com")
    )
    organization = Organization("hndrs", "https://oss.hndrs.io")
    scm = io.hndrs.gradle.plugin.Scm(
        "scm:git:git://github.com/hndrs/gradle-git-properties-plugin",
        "https://github.com/hndrs/gradle-git-properties-plugin"
    )
}



dependencies {
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.5.0.202303070854-r")

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
        property("sonar.projectKey", "hndrs_gradle-git-properties-plugin")
        property("sonar.organization", "hndrs")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.exclusions", "**/example/**")
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
    dependsOn(tasks.withType(Assemble::class.java))
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
