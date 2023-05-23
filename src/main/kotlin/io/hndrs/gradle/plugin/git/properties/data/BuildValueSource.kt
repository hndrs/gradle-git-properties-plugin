package io.hndrs.gradle.plugin.git.properties.data

import io.hndrs.gradle.plugin.git.properties.command
import io.hndrs.gradle.plugin.git.properties.filterNotNullValues
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.net.InetAddress
import javax.inject.Inject

abstract class BuildValueSource @Inject constructor(
    private val execOperations: ExecOperations
) : ValueSource<Map<String, String>, ValueSourceParameters.None> {

    override fun obtain(): Map<String, String> {
        return mapOf(
            GIT_BUILD_HOST to InetAddress.getLocalHost().hostName,
            GIT_BUILD_USER_NAME to execOperations.command("git", "config", "--get", "user.name"),
            GIT_BUILD_USER_EMAIL to execOperations.command("git", "config", "--get", "user.email"),
        ).filterNotNullValues()

    }

    companion object {
        private const val GIT_BUILD_HOST = "git.build.host"
        private const val GIT_BUILD_USER_EMAIL = "git.build.user.email"
        private const val GIT_BUILD_USER_NAME = "git.build.user.name"
    }
}