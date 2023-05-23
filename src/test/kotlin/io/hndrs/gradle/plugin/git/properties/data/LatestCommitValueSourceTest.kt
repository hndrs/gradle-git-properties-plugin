package io.hndrs.gradle.plugin.git.properties.data

import io.hndrs.gradle.plugin.git.properties.command
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LatestCommitValueSourceTest {

    val execOperations = mockk<ExecOperations>()
    val underTest = object : LatestCommitValueSource(execOperations) {
        override fun getParameters(): ValueSourceParameters.None {
            return mockk()
        }
    }

    @BeforeEach
    fun setup() {
        mockkStatic("io.hndrs.gradle.plugin.git.properties.ExecOperationsExtKt")
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `git returns no commit information`() {
        every { execOperations.command("git", "log", "-1") } returns null
        underTest.obtain() shouldBe emptyMap()
    }

    @Test
    fun `commit without email`() {
        every { execOperations.command("git", "log", "-1") } returns COMMIT_WITH_OUT_EMAIL
        underTest.obtain() shouldBe mapOf(
            "git.commit.id" to "7a9a8b9114cea8ce50e2985abe293260b91a5eab",
            "git.commit.id.abbrev" to "7a9a8b9",
            "git.commit.user.name" to "Marvin Schramm",
            "git.commit.time" to "2023-05-21T11:37:46Z",
            "git.commit.message.short" to "Initial commit",
            "git.commit.message.full" to "Initial commit"
        )
    }

    @Test
    fun `commit without author`() {
        every { execOperations.command("git", "log", "-1") } returns COMMIT_WITH_OUT_AUTHOR
        underTest.obtain() shouldBe mapOf(
            "git.commit.id" to "7a9a8b9114cea8ce50e2985abe293260b91a5eab",
            "git.commit.id.abbrev" to "7a9a8b9",
            "git.commit.message.short" to "Initial commit",
            "git.commit.message.full" to "Initial commit"
        )
    }

    @Test
    fun `commit with one line`() {
        every { execOperations.command("git", "log", "-1") } returns COMMIT_WITH_ONE_LINE
        underTest.obtain() shouldBe mapOf(
            "git.commit.id" to "7a9a8b9114cea8ce50e2985abe293260b91a5eab",
            "git.commit.id.abbrev" to "7a9a8b9",
            "git.commit.user.name" to "Marvin Schramm",
            "git.commit.user.email" to "MarvinSchramm@users.noreply.github.com",
            "git.commit.time" to "2023-05-21T11:37:46Z",
            "git.commit.message.short" to "Initial commit",
            "git.commit.message.full" to "Initial commit"
        )
    }

    @Test
    fun `commit with two lines`() {
        every { execOperations.command("git", "log", "-1") } returns COMMIT_WITH_TWO_LINES
        underTest.obtain() shouldBe mapOf(
            "git.commit.id" to "7a9a8b9114cea8ce50e2985abe293260b91a5eab",
            "git.commit.id.abbrev" to "7a9a8b9",
            "git.commit.user.name" to "Marvin Schramm",
            "git.commit.user.email" to "MarvinSchramm@users.noreply.github.com",
            "git.commit.time" to "2023-05-21T11:37:46Z",
            "git.commit.message.short" to "Initial commit",
            "git.commit.message.full" to "Initial commit\\nDescription"
        )
    }

    @Test
    fun `commit with three lines`() {
        every { execOperations.command("git", "log", "-1") } returns COMMIT_WITH_THREE_LINES
        underTest.obtain() shouldBe mapOf(
            "git.commit.id" to "7a9a8b9114cea8ce50e2985abe293260b91a5eab",
            "git.commit.id.abbrev" to "7a9a8b9",
            "git.commit.user.name" to "Marvin Schramm",
            "git.commit.user.email" to "MarvinSchramm@users.noreply.github.com",
            "git.commit.time" to "2023-05-21T11:37:46Z",
            "git.commit.message.short" to "Initial commit",
            "git.commit.message.full" to "Initial commit\\nDescription\\nYet another description"
        )
    }

    companion object {
        private val COMMIT_WITH_OUT_EMAIL = """
            commit 7a9a8b9114cea8ce50e2985abe293260b91a5eab
            Author: Marvin Schramm <>
            Date:   Sun May 21 13:37:46 2023 +0200

                Initial commit
        """.trimIndent()

        private val COMMIT_WITH_OUT_AUTHOR = """
            commit 7a9a8b9114cea8ce50e2985abe293260b91a5eab
            Date:   Sun May 21 13:37:46 2023 +0200

                Initial commit
        """.trimIndent()

        private val COMMIT_WITH_ONE_LINE = """
            commit 7a9a8b9114cea8ce50e2985abe293260b91a5eab
            Author: Marvin Schramm <MarvinSchramm@users.noreply.github.com>
            Date:   Sun May 21 13:37:46 2023 +0200

                Initial commit
        """.trimIndent()

        private val COMMIT_WITH_TWO_LINES = """
            commit 7a9a8b9114cea8ce50e2985abe293260b91a5eab
            Author: Marvin Schramm <MarvinSchramm@users.noreply.github.com>
            Date:   Sun May 21 13:37:46 2023 +0200

                Initial commit
                
                Description
        """.trimIndent()

        private val COMMIT_WITH_THREE_LINES = """
            commit 7a9a8b9114cea8ce50e2985abe293260b91a5eab
            Author: Marvin Schramm <MarvinSchramm@users.noreply.github.com>
            Date:   Sun May 21 13:37:46 2023 +0200

                Initial commit
                
                Description
                
                Yet another description
        """.trimIndent()
    }
}