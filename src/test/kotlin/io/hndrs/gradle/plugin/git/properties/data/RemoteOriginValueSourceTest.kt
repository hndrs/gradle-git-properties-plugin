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

class RemoteOriginValueSourceTest {

    val execOperations = mockk<ExecOperations>()
    val underTest = object : RemoteOriginValueSource(execOperations) {
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
        every { execOperations.command("git", "config", "--get", "remote.origin.url") } returns "git@github.com:hndrs/gradle-git-properties-plugin.git"
        underTest.obtain() shouldBe mapOf(
            "git.remote.origin.url" to "git@github.com:hndrs/gradle-git-properties-plugin.git"
        )
    }

    @Test
    fun `returns no branch name`() {
        every { execOperations.command("git", "config", "--get", "remote.origin.url") } returns null
        underTest.obtain() shouldBe emptyMap()
    }
}