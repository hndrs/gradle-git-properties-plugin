package io.hndrs.gradle.plugin.git.properties.data

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

class GitLogPropertiesProviderTest : StringSpec({

    val testSHA="beb5061f94f29901609ab3f5ab427ea18a8b85871fca519a9c0713ad99a53abd"

    val git = mockk<Git>() {
        every { log().setMaxCount(1).call() } returns listOf(
            mockk<RevCommit> {
                every { name } returns testSHA
                every { rawGpgSignature } returns ByteArray(0)
                every { fullMessage } returns "full message"
                every { shortMessage } returns "full message"
            }
        )

        }
    }

    val underTest = GitLogPropertiesProvider(git)

    "resolves git branch via git" {
        underTest.get() shouldBe mapOf(
            "git.build.user.email" to "john.smith@gradlemail.com",
            "git.build.user.name" to "John Smith",
            "git.remote.origin.url" to "git@github.com:hndrs/gradle-git-properties-plugin.git"
        )
    }

})