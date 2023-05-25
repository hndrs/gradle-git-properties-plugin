package io.hndrs.gradle.plugin.git.properties

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.PersonIdent
import java.io.File

class TestGitRepository(rootDir: File) {

    private val git = Git.init().setDirectory(rootDir).call()


    fun addCommit() =
        git.commit()
            .setAllowEmpty(true)
            .setAuthor(
                PersonIdent(
                    "name", "email"
                )
            )
            .setSign(false)
            .setMessage("My Message").call()

    fun changeGitConfigUser(value: String) = git.repository.config
        .also {
            it.load()
            it.setString("user", null, "name", value)
            it.save()
        }


}
