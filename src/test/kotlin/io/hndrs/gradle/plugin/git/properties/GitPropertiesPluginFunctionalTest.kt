package io.hndrs.gradle.plugin.git.properties

import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File


class GitPropertiesPluginFunctionalTest : StringSpec({

    var testKitDir: File = tempdir()

    var testProjectDir: File = tempdir()

    var buildFile = File(testProjectDir, "build.gradle.kts")

    var gitRepository = TestGitRepository(testProjectDir)


    beforeAny {
        testKitDir = tempdir()
        testProjectDir = tempdir()
        buildFile = File(testProjectDir, "build.gradle.kts")
        gitRepository = TestGitRepository(testProjectDir)
    }

    "apply plugin and generate properties" {
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

    "build fails when git repository is not in valid state" {
        buildFile.writeText(
            """
                plugins {
                    id("io.hndrs.git-properties")
                }
            """.trimIndent()
        )

        val runner = GradleRunner.create()
            .withTestKitDir(testKitDir)
            .withProjectDir(testProjectDir)
            .withDebug(true) // enabled for test coverage
            .withPluginClasspath()

        val buildResult = runner
            .withArguments("generateGitProperties")
            .buildAndFail()

        buildResult.output shouldContain "Execution failed for task ':generateGitProperties'."
    }

    "build does not fail when git repository is not in valid state and `stopBuildOnFailure` is set to false" {
        buildFile.writeText(
            """
                import io.hndrs.gradle.plugin.git.properties.GenerateGitPropertiesTask
                
                plugins {
                    id("io.hndrs.git-properties")
                }
                
                tasks.withType(GenerateGitPropertiesTask::class.java) {
                    stopBuildOnFailure.set(false)
                }
            """.trimIndent()
        )

        val runner = GradleRunner.create()
            .withTestKitDir(testKitDir)
            .withProjectDir(testProjectDir)
            .withPluginClasspath()

        val buildResult = runner
            .withArguments("generateGitProperties")
            .build()

        buildResult.output shouldContain "Execution failed for task ':generateGitProperties' but continuing build (stopBuildOnFailure is set to false)"
    }
})