package io.hndrs.gradle.plugin.git.properties.data

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.StoredConfig

class GitConfigPropertiesProviderTest : StringSpec({

    val storedConfig = mockk<StoredConfig>() {
        every { load() } returns Unit
    }
    val git = mockk<Git>() {
        every { repository.config } returns storedConfig
    }

    val underTest = GitConfigPropertiesProvider(git)

    "resolves git config properties (remote origin with credentials)" {

        every { storedConfig.getString("user", null, "email") } returns "john.smith@gradlemail.com"
        every { storedConfig.getString("user", null, "name") } returns "John Smith"
        every { storedConfig.getString("remote", "origin", "url") } returns "https://name:password@github.com/hndrs/gradle-git-properties-plugin.git"

        underTest.get() shouldBe mapOf(
            "git.build.user.email" to "john.smith@gradlemail.com",
            "git.build.user.name" to "John Smith",
            "git.remote.origin.url" to "https://github.com/hndrs/gradle-git-properties-plugin.git"
        )
    }

    "resolves git config properties (remote with git/ssh )" {

        every { storedConfig.getString("user", null, "email") } returns "john.smith@gradlemail.com"
        every { storedConfig.getString("user", null, "name") } returns "John Smith"
        every { storedConfig.getString("remote", "origin", "url") } returns "git@github.com/hndrs/gradle-git-properties-plugin.git"

        underTest.get() shouldBe mapOf(
            "git.build.user.email" to "john.smith@gradlemail.com",
            "git.build.user.name" to "John Smith",
            "git.remote.origin.url" to "git@github.com/hndrs/gradle-git-properties-plugin.git"
        )
    }

    "resolves git config properties (with unrecognized data)" {

        every { storedConfig.getString("user", null, "email") } returns "john.smith@gradlemail.com"
        every { storedConfig.getString("user", null, "name") } returns "John Smith"
        every { storedConfig.getString("remote", "origin", "url") } returns "someUnrelatedString"

        underTest.get() shouldBe mapOf(
            "git.build.user.email" to "john.smith@gradlemail.com",
            "git.build.user.name" to "John Smith",
            "git.remote.origin.url" to "someUnrelatedString"
        )
    }

    "resolves git config properties (with exception on parsing data)" {

        every { storedConfig.getString("user", null, "email") } returns "john.smith@gradlemail.com"
        every { storedConfig.getString("user", null, "name") } returns "John Smith"
        every { storedConfig.getString("remote", "origin", "url") } returns "https>"

        underTest.get() shouldBe mapOf(
            "git.build.user.email" to "john.smith@gradlemail.com",
            "git.build.user.name" to "John Smith",
            "git.remote.origin.url" to null
        )
    }
})