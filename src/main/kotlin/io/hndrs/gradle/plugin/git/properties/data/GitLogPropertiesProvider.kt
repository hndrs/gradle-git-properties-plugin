package io.hndrs.gradle.plugin.git.properties.data

import org.eclipse.jgit.api.Git
import java.time.Instant

class GitLogPropertiesProvider(
    private val git: Git
) : GitPropertyProvider {

    override fun get(): Map<String, Any?> {
        return git.log().setMaxCount(1).call().firstOrNull()
            ?.let {
                mapOf(
                    GIT_COMMIT_ID to it.name,
                    GIT_COMMIT_SIGNED to (it.rawGpgSignature != null).toString(),
                    GIT_COMMIT_ID_ABBREV to it.name.substring(0, 7),
                    GIT_COMMIT_MESSAGE_FULL to it.fullMessage.trim(),
                    GIT_COMMIT_MESSAGE_SHORT to it.shortMessage,
                    GIT_COMMIT_TIME to Instant.ofEpochSecond(it.commitTime.toLong()).toString(),
                    GIT_COMMIT_USER_EMAIL to it.authorIdent.emailAddress,
                    GIT_COMMIT_USER_NAME to it.authorIdent.name,
                )
            } ?: emptyMap()
    }

    companion object {
        private const val GIT_COMMIT_ID = "git.commit.id"
        private const val GIT_COMMIT_ID_ABBREV = "git.commit.id.abbrev"
        private const val GIT_COMMIT_MESSAGE_FULL = "git.commit.message.full"
        private const val GIT_COMMIT_MESSAGE_SHORT = "git.commit.message.short"
        private const val GIT_COMMIT_TIME = "git.commit.time"
        private const val GIT_COMMIT_USER_EMAIL = "git.commit.user.email"
        private const val GIT_COMMIT_USER_NAME = "git.commit.user.name"
        private const val GIT_COMMIT_SIGNED = "git.commit.signed"
    }
}