package io.hndrs.gradle.plugin.git.properties

import io.hndrs.gradle.plugin.git.properties.common.escapeLF
import io.hndrs.gradle.plugin.git.properties.data.BuildHostPropertiesProvider
import io.hndrs.gradle.plugin.git.properties.data.GitBranchPropertiesProvider
import io.hndrs.gradle.plugin.git.properties.data.GitConfigPropertiesProvider
import io.hndrs.gradle.plugin.git.properties.data.GitLogPropertiesProvider
import io.hndrs.gradle.plugin.git.properties.data.GitPropertiesProviderChain
import org.eclipse.jgit.api.Git
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject


@CacheableTask
abstract class GenerateGitPropertiesTask @Inject constructor(
    private val objectFactory: ObjectFactory,
) : DefaultTask() {

    /**
     * Input directory (defaults to .git) this can be changed if the .git folder is
     * somewhere else in the tree, but its mainly here to make this task cacheable
     */
    @get:InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    val dotGitDirectory: DirectoryProperty = objectFactory.directoryProperty().apply {
        set(File(DOT_GIT_DIRECTORY_PATH))
    }

    /**
     * Output file of this given task defaults to `/build/main/resources/git.properties`
     */
    @get:OutputFile
    val output: RegularFileProperty = gitPropertiesFileProperty()

    @TaskAction
    fun generateGitProperties() {
        runCatching {
            val git = Git.open(dotGitDirectory.asFile.get())

            val properties = GitPropertiesProviderChain.of(
                BuildHostPropertiesProvider(),
                GitBranchPropertiesProvider(git),
                GitConfigPropertiesProvider(git),
                GitLogPropertiesProvider(git),
            )
                .get()
                .also {
                    git.close()
                }


            Writer(properties).writeTo(output.asFile.get())

        }.onFailure {
            logger.error("dasdasd")
        }
    }

    private fun gitPropertiesFileProperty(): RegularFileProperty {
        return objectFactory.fileProperty().apply {
            set(File("build$BUILD_RESOURCES_PATH"))
        }
    }

    companion object {
        private const val BUILD_RESOURCES_PATH = "/resources/main/git.properties"
        private const val DOT_GIT_DIRECTORY_PATH = ".git"
    }
}

class Writer(private val properties: Map<String, Any?>) {

    fun writeTo(file: File) {
        val fileContent = with(StringBuilder()) {
            properties
                .filter { it.value != null }
                .map { it.key to it.value.toString().escapeLF() }
                .forEach {
                    if (it.second.isNotBlank()) {
                        appendLine("${it.first}=${it.second}")
                    }
                }
            this.toString()
        }
        file.writeText(fileContent)
    }
}