package io.hndrs.gradle.plugin.git.properties.data

import org.eclipse.jgit.api.Git
import java.net.URI

class GitConfigPropertiesProvider(
    private val git: Git
) : GitPropertyProvider {

    override fun get(): Map<String, Any?> {
        return git.repository.config.also {
            it.load()
        }.let {
            mapOf(
                GIT_BUILD_USER_EMAIL to it.getString("user", null, "email"),
                GIT_BUILD_USER_NAME to it.getString("user", null, "name"),
                GIT_REMOTE_ORIGIN_URL to it.getString("remote", "origin", "url")?.sanitise()
            )
        }
    }

    private fun String.sanitise(): String? {
        return runCatching {
            URI(this).userInfo?.let {
                this.replace(it, "").replace("@", "")
            } ?: this
        }.getOrElse { null }

    }

    companion object {
        private const val GIT_REMOTE_ORIGIN_URL = "git.remote.origin.url"
        private const val GIT_BUILD_USER_EMAIL = "git.build.user.email"
        private const val GIT_BUILD_USER_NAME = "git.build.user.name"
    }
}