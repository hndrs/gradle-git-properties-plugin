package io.hndrs.gradle.plugin.git.properties.data

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.revwalk.RevCommit
import java.time.Instant
import java.time.temporal.ChronoUnit

class GitLogPropertiesProviderTest : StringSpec({

    val testSHA = "beb5061f94f29901609ab3f5ab427ea18a8b85871fca519a9c0713ad99a53abd"
    val testCommitTime = Instant.now().truncatedTo(ChronoUnit.SECONDS)
    val testAuthor = PersonIdent("John Smith", "john.smith@gradlemail.com")
    val testFullMessage = """
                                Full Message
                    
                                    Another Nice Message
                                """.trimIndent()

    val git = mockk<Git>() {
        every { log().setMaxCount(1).call() } returns listOf(
            mockk<RevCommit> {
                every { name } returns testSHA
                every { rawGpgSignature } returns ByteArray(0)
                every { fullMessage } returns testFullMessage
                every { shortMessage } returns testFullMessage.lines().first()
                every { commitTime } returns testCommitTime.epochSecond.toInt()
                every { authorIdent } returns testAuthor
            }
        )
    }


    val underTest = GitLogPropertiesProvider(git)

    "resolves git branch via git" {
        underTest.get() shouldBe mapOf(
            "git.commit.id" to testSHA,
            "git.commit.id.abbrev" to testSHA.substring(0, 7),
            "git.commit.message.full" to testFullMessage,
            "git.commit.message.short" to testFullMessage.lines().first(),
            "git.commit.time" to testCommitTime,
            "git.commit.user.email" to testAuthor.emailAddress,
            "git.commit.user.name" to testAuthor.name,
            "git.commit.signed" to true,
        )
    }

})