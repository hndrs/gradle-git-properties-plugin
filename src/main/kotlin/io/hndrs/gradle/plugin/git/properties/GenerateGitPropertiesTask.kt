package io.hndrs.gradle.plugin.git.properties

import io.hndrs.gradle.plugin.git.properties.data.BranchNameValueSource
import io.hndrs.gradle.plugin.git.properties.data.BuildValueSource
import io.hndrs.gradle.plugin.git.properties.data.LatestCommitValueSource
import io.hndrs.gradle.plugin.git.properties.data.RemoteOriginValueSource
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ProviderFactory
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
    private val providerFactory: ProviderFactory
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
        val fileContent = StringBuilder().apply {
            getProperties().forEach {
                appendLine("${it.key}=${it.value}")
            }
        }.toString()

        output.asFile.get().run {
            this.createNewFile()
            writeText(fileContent)
        }
    }

    private fun getProperties(): Map<String, String> {
        return listOf(
            providerFactory.of(LatestCommitValueSource::class.java) {}.get(),
            providerFactory.of(BranchNameValueSource::class.java) {}.get(),
            providerFactory.of(RemoteOriginValueSource::class.java) {}.get(),
            providerFactory.of(BuildValueSource::class.java) {}.get()
        ).flatMap { it.entries }.map {
            it.toPair()
        }.toMap()
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