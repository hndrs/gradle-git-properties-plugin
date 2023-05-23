package io.hndrs.gradle.plugin.git.properties.data

import io.hndrs.gradle.plugin.git.properties.command
import io.hndrs.gradle.plugin.git.properties.filterNotNullValues
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import javax.inject.Inject

abstract class BranchNameValueSource @Inject constructor(
    private val execOperations: ExecOperations
) : ValueSource<Map<String, String>, ValueSourceParameters.None> {

    override fun obtain(): Map<String, String> {
        return mapOf(
            GIT_BRANCH to execOperations.command("git", "branch", "--show-current")
        ).filterNotNullValues()
    }

    companion object {
        private const val GIT_BRANCH = "git.branch"
    }
}