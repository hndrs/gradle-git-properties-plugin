package io.hndrs.gradle.plugin.git.properties

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
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.api.tasks.options.Option
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

    @Option(option = "continue-on-error", description = "Continues build on failure ")
    @get:Input
    val continueOnError: Property<Boolean> = objectFactory.property(Boolean::class.java)
        .convention(false)


    /**
     * Output file of this given task defaults to `/build/main/resources/git.properties`
     */
    @get:OutputFile
    val output: RegularFileProperty = gitPropertiesFileProperty()

    @TaskAction
    fun generateGitProperties() {
        logger.error("Continue on failure $continueOnError")
        runCatching {
            val git = Git.open(dotGitDirectory.asFile.get())
            val properties = GitPropertiesProviderChain.of(
                GitBranchPropertiesProvider(git),
                GitConfigPropertiesProvider(git),
                GitLogPropertiesProvider(git),
                BuildHostPropertiesProvider(),
            ).get().also {
                git.close()
            }

            PropertiesFileWriter(properties).writeTo(output.asFile.get())

        }.onFailure {
            if (continueOnError.get()) {
                logger.error(
                    """
                        Execution failed for task ':generateGitProperties' but continuing build with --continue-on-error
                        > ${it.message}
                    """.trimIndent()
                )
            } else {
                throw TaskExecutionException(this, it)
            }

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

