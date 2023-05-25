package io.hndrs.gradle.plugin.git.properties.data

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.net.InetAddress

class BuildHostPropertiesProviderTest : StringSpec({

    val underTest = BuildHostPropertiesProvider()

    "resolves host provider" {
        underTest.get() shouldBe mapOf(
            "git.build.host" to InetAddress.getLocalHost().hostName
        )
    }

})