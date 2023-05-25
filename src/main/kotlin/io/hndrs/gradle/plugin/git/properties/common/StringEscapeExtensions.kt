package io.hndrs.gradle.plugin.git.properties.common

fun String.escapeLF(): String {
    return this.replace("\n", "\\n")
}