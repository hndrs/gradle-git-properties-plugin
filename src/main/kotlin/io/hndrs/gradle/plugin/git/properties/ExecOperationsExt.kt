package io.hndrs.gradle.plugin.git.properties

import org.gradle.process.ExecOperations
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset

/**
 * Convenience extension functions to get the output of a command directly as a string
 */
internal fun ExecOperations.command(vararg args: Any): String? {
    val output = ByteArrayOutputStream()
    this.exec {
        commandLine(*args)
        standardOutput = output
    }
    return String(output.toByteArray(), Charset.defaultCharset()).trim().also {
        println(it)
    }.takeIf {
        it.isNotBlank()
    }
}