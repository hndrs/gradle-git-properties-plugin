package io.hndrs.gradle.plugin.git.properties.data

import java.time.Instant

data class CommitRef(
    val sha: String,
    val author: Author,
    val commitTime: Instant,
    val message: Message
) {

    fun shortSha(): String {
        return sha.substring(0, 7)
    }

    data class Author(
        val name: String,
        val email: String?,
    )

    data class Message(
        val short: String,
        val full: String
    )
}