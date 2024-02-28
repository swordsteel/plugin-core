package ltd.lulz.plugin

import ltd.lulz.plugin.extension.CoreExtension
import ltd.lulz.plugin.extension.GitExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class CorePlugin : Plugin<Project> {

    /**
     * Register Extensions and Tasks.
     */
    override fun apply(project: Project) {
        coreExtension(project)
        gitExtension(project)
    }

    private fun gitExtension(project: Project): GitExtension = project.extensions
        .create(GitExtension.PLUGIN_NAME, GitExtension::class.java, project)

    private fun coreExtension(project: Project): CoreExtension = project.extensions
        .create(CoreExtension.PLUGIN_NAME, CoreExtension::class.java, project)
}
