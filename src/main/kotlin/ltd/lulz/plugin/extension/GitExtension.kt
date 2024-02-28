package ltd.lulz.plugin.extension

import org.eclipse.jgit.api.Git.open
import org.eclipse.jgit.lib.Constants.HEAD
import org.gradle.api.Project

abstract class GitExtension(private val project: Project) {

    companion object {
        const val PLUGIN_NAME = "git"

        private const val HASH_LENGTH = 8
        private const val SNAPSHOT = "-SNAPSHOT"
        private const val UNAVAILABLE = "n/a"
        private val PRIMARY_BRANCHES = setOf("master", "develop")
    }

    fun version(): String = when {
        isHead() || currentBranch() in PRIMARY_BRANCHES -> project.version.toString()
        else -> makeVersion(project.version.toString(), currentShortHash())
    }

    fun currentShortHash(): String = open(project.projectDir)
        .use { it.repository.exactRef(HEAD)?.objectId?.name?.take(HASH_LENGTH) ?: UNAVAILABLE }

    fun currentBranch(): String = open(project.projectDir)
        .use { it.repository.branch ?: UNAVAILABLE }

    fun isHead(): Boolean = open(project.projectDir)
        .use { it.repository.exactRef(HEAD)?.target?.name == HEAD }

    private fun makeVersion(version: String, shortHash: String): String = when {
        shortHash == UNAVAILABLE -> version.also { println("Failed to get data from GIT") }
        version.endsWith(SNAPSHOT) -> version.replace(SNAPSHOT, ".$shortHash$SNAPSHOT")
        else -> version.also { println("Failed version missing suffix $SNAPSHOT") }
    }
}
