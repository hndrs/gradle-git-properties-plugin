package io.hndrs.gradle.plugin.git.properties.functional

import io.hndrs.gradle.plugin.git.properties.TestGitRepository
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File

class GeneratesPropertiesOnClassesTask : StringSpec({

    val testKitDir: File = tempdir()

    val testProjectDir: File = tempdir()

    val buildFile = File(testProjectDir, "build.gradle.kts")

    val gitRepository = TestGitRepository(testProjectDir)

    "generateGitProperties are generated on `classes` lifecycle task when java plugin is available" {
        buildFile.writeText(
            """
                plugins {
                    java
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

        runner.withArguments("classes")
            .build().task(":generateGitProperties")?.outcome shouldBe TaskOutcome.SUCCESS

        assertSoftly(File(testProjectDir, "build/resources/main/git.properties")) {
            exists() shouldBe true
        }

    }

})