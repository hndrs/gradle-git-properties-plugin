package io.hndrs.gradle.plugin.git.properties

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File


class GitPropertiesPluginFunctionalTest : StringSpec({

    val testKitDir: File = tempdir()

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

    val gitRepository = TestGitRepository(testProjectDir).also {
        it.addCommit()
    }

    "apply plugin and generate properties" {

        val runner = GradleRunner.create()
            .withTestKitDir(testKitDir)
            .withProjectDir(testProjectDir)
            .withDebug(true) // enabled for test coverage
            .withPluginClasspath()

        runner.withArguments("--build-cache", "generateGitProperties")
            .build().task(":generateGitProperties")?.outcome shouldBe TaskOutcome.SUCCESS

        runner.withArguments("--build-cache", "generateGitProperties")
            .build().task(":generateGitProperties")?.outcome shouldBe TaskOutcome.UP_TO_DATE

        File(testProjectDir, "build").deleteRecursively()

        runner.withArguments("--build-cache", "generateGitProperties")
            .build().task(":generateGitProperties")?.outcome shouldBe TaskOutcome.FROM_CACHE

        //add a new commit to invalidate cache
        gitRepository.addCommit()

        runner.withArguments("--build-cache", "generateGitProperties")
            .build().task(":generateGitProperties")?.outcome shouldBe TaskOutcome.SUCCESS

        File(testProjectDir, "build/resources/main/git.properties").exists() shouldBe true
    }
})