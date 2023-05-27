package io.hndrs.gradle.plugin.git.properties.data

import org.eclipse.jgit.api.Git

class GitBranchPropertiesProvider(
    private val git: Git,
    private val branchName: String?
) : GitPropertyProvider {

    override fun get(): Map<String, Any?> {
        return branchName?.let {
            mapOf(GIT_BRANCH to branchName)
        } ?: resolveBranchName()?.let {
            mapOf(GIT_BRANCH to it)
        } ?: mapOf(GIT_BRANCH to git.repository.branch)
    }

    private fun resolveBranchName(): String? {
        return CI_ENV_BRANCH_MAPPING.entries.firstNotNullOfOrNull {
            if (EnvProvider.getEnv(it.key) != null) {
                it.value.firstNotNullOfOrNull { EnvProvider.getEnv(it) }
            } else null
        }
    }

    /**
     * Wrapper object for mocking
     */
    object EnvProvider {
        fun getEnv(name: String): String? = System.getenv(name)
    }

    companion object {
        private const val GIT_BRANCH = "git.branch"
        private val CI_ENV_BRANCH_MAPPING = mapOf(
            "GITLAB_CI" to arrayOf("CI_COMMIT_REF_NAME"),
            "CI" to arrayOf("GITHUB_HEAD_REF", "GITHUB_REF_NAME"),
            "TRAVIS" to arrayOf("TRAVIS_BRANCH")
        )
    }
}