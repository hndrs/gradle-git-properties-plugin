package io.hndrs.gradle.plugin.git.properties

import io.hndrs.gradle.plugin.git.properties.common.escapeLF
import java.io.File

class PropertiesFileWriter(private val properties: Map<String, Any?>) {

    fun writeTo(file: File) {
        val fileContent = with(StringBuilder()) {
            properties
                .filter { it.value != null }
                .map { it.key to it.value.toString().escapeLF() }
                .sortedBy { it.first }
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