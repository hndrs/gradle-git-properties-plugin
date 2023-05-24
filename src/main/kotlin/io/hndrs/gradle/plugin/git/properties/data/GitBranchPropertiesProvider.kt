package io.hndrs.gradle.plugin.git.properties.data

import org.eclipse.jgit.api.Git

class GitBranchPropertiesProvider(
    private val git: Git
) : GitPropertyProvider {

    override fun get(): Map<String, Any?> {
        return mapOf(GIT_BRANCH to git.repository.branch)
    }

    companion object {
        private const val GIT_BRANCH = "git.branch"
    }
}