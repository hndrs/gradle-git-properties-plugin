package io.hndrs.gradle.plugin.git.properties.functional

import io.hndrs.gradle.plugin.git.properties.TestGitRepository
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.spec.tempdir
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import java.io.File

class GeneratesPropertiesWithBranchNameTest : StringSpec({

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

    "generate properties with branch-name option" {
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

        runner.withArguments("generateGitProperties", "--branch-name=test-branch")
            .build().task(":generateGitProperties")?.outcome shouldBe TaskOutcome.SUCCESS

        assertSoftly(File(testProjectDir, "build/resources/main/git.properties")) {
            exists() shouldBe true
            readText() shouldContain "git.branch=test-branch"
        }
    }

    "generate properties with git" {
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

        runner.withArguments("generateGitProperties")
            .build().task(":generateGitProperties")?.outcome shouldBe TaskOutcome.SUCCESS

        assertSoftly(File(testProjectDir, "build/resources/main/git.properties")) {
            exists() shouldBe true
            readText() shouldContain "git.branch=${gitRepository.branch()}"
        }
    }

    "generate properties with gitlab-ci envs" {
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
            .withEnvironment(
                mapOf(
                    "GITLAB_CI" to "true",
                    "CI_COMMIT_REF_NAME" to "gitlab-ci-branch"
                )
            )
            .withPluginClasspath()

        runner.withArguments("generateGitProperties")
            .build().task(":generateGitProperties")?.outcome shouldBe TaskOutcome.SUCCESS

        assertSoftly(File(testProjectDir, "build/resources/main/git.properties")) {
            exists() shouldBe true
            readText() shouldContain "git.branch=gitlab-ci-branch"
        }
    }

    "generate properties with travis-ci envs" {
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
            .withEnvironment(
                mapOf(
                    "TRAVIS" to "true",
                    "TRAVIS_BRANCH" to "travis-branch"
                )
            )
            .withPluginClasspath()

        runner.withArguments("generateGitProperties")
            .build().task(":generateGitProperties")?.outcome shouldBe TaskOutcome.SUCCESS

        assertSoftly(File(testProjectDir, "build/resources/main/git.properties")) {
            exists() shouldBe true
            readText() shouldContain "git.branch=travis-branch"
        }
    }

    "generate properties with github-actions (GITHUB_HEAD_REF) env" {
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
            .withEnvironment(
                mapOf(
                    "CI" to "true",
                    "GITHUB_HEAD_REF" to "github-head-ref-branch"
                )
            )
            .withPluginClasspath()

        runner.withArguments("generateGitProperties")
            .build().task(":generateGitProperties")?.outcome shouldBe TaskOutcome.SUCCESS

        assertSoftly(File(testProjectDir, "build/resources/main/git.properties")) {
            exists() shouldBe true
            readText() shouldContain "git.branch=github-head-ref-branch"
        }
    }

    "generate properties with github-actions (GITHUB_REF_NAME) env" {
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
            .withEnvironment(
                mapOf(
                    "CI" to "true",
                    "GITHUB_REF_NAME" to "github-ref-name-branch"
                )
            )
            .withPluginClasspath()

        runner.withArguments("generateGitProperties")
            .build().task(":generateGitProperties")?.outcome shouldBe TaskOutcome.SUCCESS

        assertSoftly(File(testProjectDir, "build/resources/main/git.properties")) {
            exists() shouldBe true
            readText() shouldContain "git.branch=github-ref-name-branch"
        }
    }
})