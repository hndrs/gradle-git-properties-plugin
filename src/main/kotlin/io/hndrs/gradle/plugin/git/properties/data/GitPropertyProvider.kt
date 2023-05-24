package io.hndrs.gradle.plugin.git.properties.data

interface GitPropertyProvider {

    fun get(): Map<String, Any?>
}