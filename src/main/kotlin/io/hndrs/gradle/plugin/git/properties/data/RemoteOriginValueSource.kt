package io.hndrs.gradle.plugin.git.properties.data

import io.hndrs.gradle.plugin.git.properties.command
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * Value source that provides the git repositories remote origin via the
 * `git config --get remote.origin.url` command. This requires a git installation
 * to be installed
 */
abstract class RemoteOriginValueSource @Inject constructor(
    private val execOperations: ExecOperations
) : ValueSource<Map<String, String>, ValueSourceParameters.None> {

    override fun obtain(): Map<String, String> {
        return execOperations.command("git", "config", "--get", "remote.origin.url")?.let {
            mapOf(GIT_REMOTE_ORIGIN_URL to it)
        } ?: emptyMap()
    }

    companion object {
        private const val GIT_REMOTE_ORIGIN_URL = "git.remote.origin.url"
    }
}