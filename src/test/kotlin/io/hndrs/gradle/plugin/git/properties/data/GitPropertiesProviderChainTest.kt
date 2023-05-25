package io.hndrs.gradle.plugin.git.properties.data

import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainAll

class GitPropertiesProviderChainTest : StringSpec({


    "gets all values from all providers" {
        GitPropertiesProviderChain.of(
            GitPropertyProvider { mapOf("key" to "value") },
            GitPropertyProvider { mapOf("key1" to "value1") },
            GitPropertyProvider { mapOf("key2" to "value2") },
            GitPropertyProvider { mapOf("key3" to 3) },
        ).get() shouldContainAll mapOf(
            "key" to "value",
            "key1" to "value1",
            "key2" to "value2",
            "key3" to 3,
        )
    }

    "sorts all values from all providers" {
        assertSoftly(
            GitPropertiesProviderChain.of(
                GitPropertyProvider { mapOf("key3" to 3) },
                GitPropertyProvider { mapOf("key2" to "value2") },
                GitPropertyProvider { mapOf("key" to "value") },
                GitPropertyProvider { mapOf("key1" to "value1") },
            ).get().entries
        ) {
        }
    }
})