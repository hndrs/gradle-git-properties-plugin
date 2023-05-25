package io.hndrs.gradle.plugin.git.properties.functional

import io.hndrs.gradle.plugin.git.properties.TestGitRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.GradleRunner
import java.io.File


class BuildFailsOnInvalidGitRepository : StringSpec({

    var testKitDir: File = tempdir()

    var testProjectDir: File = tempdir()

    var buildFile = File(testProjectDir, "build.gradle.kts")



    beforeAny {
        testKitDir = tempdir()
        testProjectDir = tempdir()
        buildFile = File(testProjectDir, "build.gradle.kts")
    }

    "build fails when git repository is not in valid state" {
        TestGitRepository(testProjectDir) // creates .git folder
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

    "build does not fail when git repository is not in valid state with --continue-on-failure" {
        TestGitRepository(testProjectDir) // creates .git folder
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
            .withArguments("generateGitProperties", "--continue-on-error")
            .build()

        buildResult.output shouldContain "Execution failed for task ':generateGitProperties' but continuing build with --continue-on-error"
    }
})