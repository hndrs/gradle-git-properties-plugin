package io.hndrs.gradle.plugin.git.properties.data

import io.hndrs.gradle.plugin.git.properties.command
import io.hndrs.gradle.plugin.git.properties.filterNotNullValues
import org.gradle.api.provider.ValueSource
import org.gradle.api.provider.ValueSourceParameters
import org.gradle.process.ExecOperations
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

abstract class LatestCommitValueSource @Inject constructor(
    private val execOperations: ExecOperations
) : ValueSource<Map<String, String>, ValueSourceParameters.None> {
    override fun obtain(): Map<String, String> {

        return execOperations.command("git", "log", "-1")?.let {
            mapOf(
                GIT_COMMIT_ID to it.commitId(),
                GIT_COMMIT_ID_ABBREV to it.commitId().substring(0, 7),
                GIT_COMMIT_USER_NAME to it.commitAuthorName(),
                GIT_COMMIT_USER_EMAIL to it.commitAuthorEmail(),
                GIT_COMMIT_TIME to it.commitTime(),
                GIT_COMMIT_MESSAGE_SHORT to it.commitShortMessage(),
                GIT_COMMIT_MESSAGE_FULL to it.commitFullMessage()
            ).filterNotNullValues()
        } ?: emptyMap()
    }

    private fun String.commitId(): String {
        return with(this.lines()[0]) {
            substring("commit".length).trim()
        }
    }

    private fun String.commitAuthorName(): String? {
        return lines().getOrNull(1)?.let {
            authorRegex.findAndGet(it, 1) { it }
        }
    }

    private fun String.commitAuthorEmail(): String? {
        return lines().getOrNull(1)?.let {
            authorRegex.findAndGet(it, 2) { it }
        }
    }

    private fun String.commitTime(): String? {
        return lines().getOrNull(2)?.let {
            dateRegex.findAndGet(it, 1) {
                ZonedDateTime.parse(it, formatter.withZone(utcZone))
                    .toInstant()
                    .toString()
            }
        }
    }

    private fun String.commitShortMessage(): String? {
        return with(lines()) {
            this.subList(3, this.size)
                .map { it.trim() }
                .firstOrNull { it.isNotBlank() }
        }
    }

    private fun String.commitFullMessage(): String {
        return with(lines()) {
            this.subList(3, this.size)
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .joinToString(separator = "\\n")
        }
    }

    /**
     * Finds Values via regex and returns value of group (zero based)
     * optionally directly transforms value
     * returns [null] if group does not exist or if string is blank
     */
    private fun <T> Regex.findAndGet(
        value: String,
        group: Int,
        transform: (String) -> T
    ): T? {
        return find(value)?.groupValues?.getOrNull(group)?.takeIf { it.isNotBlank() }?.let(transform)
    }

    companion object {
        private const val GIT_COMMIT_ID = "git.commit.id"
        private const val GIT_COMMIT_ID_ABBREV = "git.commit.id.abbrev"
        private const val GIT_COMMIT_MESSAGE_FULL = "git.commit.message.full"
        private const val GIT_COMMIT_MESSAGE_SHORT = "git.commit.message.short"
        private const val GIT_COMMIT_TIME = "git.commit.time"
        private const val GIT_COMMIT_USER_EMAIL = "git.commit.user.email"
        private const val GIT_COMMIT_USER_NAME = "git.commit.user.name"

        private val authorRegex = Regex("Author: (.*) <(.*)>")
        private val dateRegex = Regex("Date:\\s+(.*)")
        private val utcZone = ZoneId.of("UTC")
        private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss yyyy Z")
    }
}