package io.hndrs.gradle.plugin.git.properties

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.plugins.JavaPlugin

class GitPropertiesPlugin : Plugin<Project> {
    override fun apply(target: Project) {

        val task = target.tasks.register("generateGitProperties", GenerateGitPropertiesTask::class.java) {
            group = BasePlugin.BUILD_GROUP
        }
        /**
         * If the java plugin is available we hook the task into the `classes` lifecycle task
         */
        target.plugins.withType(JavaPlugin::class.java) {
            target.tasks.named(JavaPlugin.CLASSES_TASK_NAME) {
                dependsOn(task)
            }
        }
    }
}