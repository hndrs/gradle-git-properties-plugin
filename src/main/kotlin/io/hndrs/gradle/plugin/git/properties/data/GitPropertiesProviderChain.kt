package io.hndrs.gradle.plugin.git.properties.data

class GitPropertiesProviderChain private constructor(
    private val providers: Set<GitPropertyProvider>
) : GitPropertyProvider {
    override fun get(): Map<String, Any?> {
        return providers.flatMap {
            it.get().entries
        }.filter {
            it.value != null
        }.map {
            it.toPair()
        }.sortedBy {
            it.first
        }.toMap()
    }

    companion object {
        fun of(vararg providers: GitPropertyProvider): GitPropertiesProviderChain {
            return GitPropertiesProviderChain(providers.toSet())
        }
    }

}

