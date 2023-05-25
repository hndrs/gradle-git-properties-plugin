package io.hndrs.gradle.plugin.git.properties

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.File

class PropertiesFileWriterTest : StringSpec({

    "writes properties to file" {
        val tempFile = File.createTempFile("git", "properties")

        PropertiesFileWriter(
            mapOf(
                "key1" to "value1",
                "key2" to "value2",
                "key3" to "value3",
                "key4" to "value4"
            )
        ).writeTo(tempFile)

        tempFile.readText() shouldBe """
                                        key1=value1
                                        key2=value2
                                        key3=value3
                                        key4=value4
                                        
                                    """.trimIndent()

        tempFile.delete()
    }

    "escapes multi line values" {
        val tempFile = File.createTempFile("git", "properties")

        PropertiesFileWriter(
            mapOf(
                "key1" to """
                    MultiLine
                    Value
                """.trimIndent(),
                "key2" to "value2",
            )
        ).writeTo(tempFile)

        tempFile.readText() shouldBe """
                                        key1=MultiLine\nValue
                                        key2=value2
                                        
                                    """.trimIndent()

        tempFile.delete()
    }

    "sorts properties by key (asc)" {
        val tempFile = File.createTempFile("git", "properties")

        PropertiesFileWriter(
            mapOf(
                "key.a" to "valueA",
                "key.c" to "valueC",
                "key.f" to "valueF",
                "key.b" to "valueB",
            )
        ).writeTo(tempFile)

        tempFile.readText() shouldBe """
                                        key.a=valueA
                                        key.b=valueB
                                        key.c=valueC
                                        key.f=valueF
                                        
                                    """.trimIndent()

        tempFile.delete()
    }
})