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

class BranchNameValueSourceTest {

    val execOperations = mockk<ExecOperations>()
    val underTest = object : BranchNameValueSource(execOperations) {
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
    fun `returns branch name`() {
        every { execOperations.command("git", "branch", "--show-current") } returns "branch-name"
        underTest.obtain() shouldBe mapOf(
            "git.branch" to "branch-name"
        )
    }

    @Test
    fun `returns no branch name`() {
        every { execOperations.command("git", "branch", "--show-current") } returns null
        underTest.obtain() shouldBe emptyMap()
    }
}