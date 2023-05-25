package io.hndrs.gradle.plugin.git.properties.data

fun interface GitPropertyProvider {

    fun get(): Map<String, Any?>
}