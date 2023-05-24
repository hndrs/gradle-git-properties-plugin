package io.hndrs.gradle.plugin.git.properties

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File


class GitPropertiesPluginFunctionalTest : StringSpec({


    val testProjectDir: File = tempdir()

    File(testProjectDir, "build.gradle.kts").apply {
        writeText(
            """
                plugins {
                    id("io.hndrs.git-properties")
                }
                """.trimIndent()
        )
    }

    val gitRepository = TestGitRepository(testProjectDir)

    val commit = gitRepository.addCommit()
    "apply plugin and generate properties" {

        val build = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments("generateGitProperties")
            .withDebug(true) // enabled for test coverage
            .withPluginClasspath()
            .build()

        build.task(":generateGitProperties")?.outcome shouldBe TaskOutcome.SUCCESS

        File(testProjectDir, "build/resources/main/git.properties").exists() shouldBe true
    }
})