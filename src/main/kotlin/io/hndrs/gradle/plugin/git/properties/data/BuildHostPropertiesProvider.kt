package io.hndrs.gradle.plugin.git.properties.data

import java.net.InetAddress

class BuildHostPropertiesProvider : GitPropertyProvider {

    override fun get(): Map<String, Any?> {
        return mapOf(GIT_BUILD_HOST to InetAddress.getLocalHost().hostName)
    }

    companion object {
        private const val GIT_BUILD_HOST = "git.build.host"
    }
}