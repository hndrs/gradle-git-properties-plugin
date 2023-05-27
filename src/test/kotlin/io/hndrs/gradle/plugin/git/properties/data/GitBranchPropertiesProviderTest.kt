package io.hndrs.gradle.plugin.git.properties.data

import io.hndrs.gradle.plugin.git.properties.data.GitBranchPropertiesProvider.EnvProvider
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import org.eclipse.jgit.api.Git

class GitBranchPropertiesProviderTest : StringSpec({

    val git = mockk<Git>() {
        every { repository.branch } returns "git-main"
    }

    "resolves git branch via branch-name-option" {
        val underTest = GitBranchPropertiesProvider(git, "main")
        underTest.get() shouldBe mapOf(
            "git.branch" to "main"
        )
    }

    "resolves git branch via git" {
        val underTest = GitBranchPropertiesProvider(git, null)
        underTest.get() shouldBe mapOf(
            "git.branch" to "git-main"
        )
    }

    "resolves git branch for github actions via env GITHUB_HEAD_REF" {
        val underTest = GitBranchPropertiesProvider(git, null)
        mockkObject(EnvProvider)
        every { EnvProvider.getEnv("CI") } returns "true"
        every { EnvProvider.getEnv("GITHUB_HEAD_REF") } returns "head-ref-main"

        underTest.get() shouldBe mapOf(
            "git.branch" to "head-ref-main"
        )
    }

    "resolves git branch for github actions via env GITHUB_REF_NAME" {
        val underTest = GitBranchPropertiesProvider(git, null)
        mockkObject(EnvProvider)
        every { EnvProvider.getEnv("CI") } returns "true"
        every { EnvProvider.getEnv("GITHUB_REF_NAME") } returns "ref-main"

        underTest.get() shouldBe mapOf(
            "git.branch" to "ref-main"
        )
    }

    "resolves git branch for gitlab-ci via env CI_COMMIT_REF_NAME" {
        val underTest = GitBranchPropertiesProvider(git, null)
        mockkObject(EnvProvider)
        every { EnvProvider.getEnv("GITLAB_CI") } returns "true"
        every { EnvProvider.getEnv("CI_COMMIT_REF_NAME") } returns "gitlab-main"

        underTest.get() shouldBe mapOf(
            "git.branch" to "gitlab-main"
        )
    }

    "resolves git branch for travis-ci via env TRAVIS_BRANCH" {
        val underTest = GitBranchPropertiesProvider(git, null)
        mockkObject(EnvProvider)
        every { EnvProvider.getEnv("TRAVIS") } returns "true"
        every { EnvProvider.getEnv("TRAVIS_BRANCH") } returns "travis-main"

        underTest.get() shouldBe mapOf(
            "git.branch" to "travis-main"
        )
    }

})