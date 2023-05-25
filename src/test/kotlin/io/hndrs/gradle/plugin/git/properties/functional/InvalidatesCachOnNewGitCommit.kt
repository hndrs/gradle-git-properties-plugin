package io.hndrs.gradle.plugin.git.properties.functional

import io.hndrs.gradle.plugin.git.properties.TestGitRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File

class InvalidatesCachOnNewGitCommit : StringSpec({

    val testKitDir: File = tempdir()

    val testProjectDir: File = tempdir()

    val buildFile = File(testProjectDir, "build.gradle.kts")

    val gitRepository = TestGitRepository(testProjectDir)


    "cache is invalidated on new commit" {
        buildFile.writeText(
            """
                plugins {
                    id("io.hndrs.git-properties")
                }
            """.trimIndent()
        )

        // add first commit
        gitRepository.addCommit()
        val runner = GradleRunner.create()
            .withTestKitDir(testKitDir)
            .withProjectDir(testProjectDir)
            .withDebug(true) // enabled for test coverage
            .withPluginClasspath()

        runner.withArguments("--build-cache", "generateGitProperties")
            .build().task(":generateGitProperties")?.outcome shouldBe TaskOutcome.SUCCESS

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