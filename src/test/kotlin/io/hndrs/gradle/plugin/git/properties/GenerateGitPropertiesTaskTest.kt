package io.hndrs.gradle.plugin.git.properties

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldNotBe
import org.gradle.kotlin.dsl.withType
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class GenerateGitPropertiesPluginTest {

    val project = ProjectBuilder.builder().build().also {
        it.plugins.apply(GitPropertiesPlugin::class.java)
    }

    @Test
    fun `generate properties task is configured`() {
        assertSoftly(project.tasks.withType<GenerateGitPropertiesTask>().first()) {
            this.dotGitDirectory.asFile.orNull shouldNotBe null
            this.output.asFile.orNull shouldNotBe null
        }
    }
}