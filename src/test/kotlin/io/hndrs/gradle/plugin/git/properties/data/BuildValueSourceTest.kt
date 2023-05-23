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
import java.net.InetAddress

class BuildValueSourceTest {

    val execOperations = mockk<ExecOperations>()
    val underTest = object : BuildValueSource(execOperations) {
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
    fun `git returns no user information`() {
        every { execOperations.command("git", "config", "--get", "user.name") } returns null
        every { execOperations.command("git", "config", "--get", "user.email") } returns null
        underTest.obtain() shouldBe mapOf(
            "git.build.host" to InetAddress.getLocalHost().hostName
        )
    }

    @Test
    fun `git returns all user information`() {
        every { execOperations.command("git", "config", "--get", "user.name") } returns "name"
        every { execOperations.command("git", "config", "--get", "user.email") } returns "email"
        underTest.obtain() shouldBe mapOf(
            "git.build.host" to InetAddress.getLocalHost().hostName,
            "git.build.user.name" to "name",
            "git.build.user.email" to "email",
        )
    }
}