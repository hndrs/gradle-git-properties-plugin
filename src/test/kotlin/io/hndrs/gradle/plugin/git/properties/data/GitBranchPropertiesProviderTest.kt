package io.hndrs.gradle.plugin.git.properties.data

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.eclipse.jgit.api.Git

class GitBranchPropertiesProviderTest : StringSpec({

    val git = mockk<Git>() {
        every { repository.branch } returns "main"
    }

    val underTest = GitBranchPropertiesProvider(git)

    "resolves git branch via git" {
        underTest.get() shouldBe mapOf(
            "git.branch" to "main"
        )
    }

})