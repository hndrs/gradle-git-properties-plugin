package io.hndrs.gradle.plugin.git.properties.data

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.eclipse.jgit.api.Git

class GitConfigPropertiesProviderTest : StringSpec({

    val git = mockk<Git>() {
        every { repository.config } returns mockk(relaxed = true) {
            every { getString("user", null, "email") } returns "john.smith@gradlemail.com"
            every { getString("user", null, "name") } returns "John Smith"
            every { getString("remote", "origin", "url") } returns "git@github.com:hndrs/gradle-git-properties-plugin.git"
        }
    }

    val underTest = GitConfigPropertiesProvider(git)

    "resolves git branch via git" {
        underTest.get() shouldBe mapOf(
            "git.build.user.email" to "john.smith@gradlemail.com",
            "git.build.user.name" to "John Smith",
            "git.remote.origin.url" to "git@github.com:hndrs/gradle-git-properties-plugin.git"
        )
    }

})