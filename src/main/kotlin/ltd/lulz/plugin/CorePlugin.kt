package ltd.lulz.plugin

import ltd.lulz.plugin.extension.CoreExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class CorePlugin : Plugin<Project> {

    /**
     * Register Extensions and Tasks.
     */
    override fun apply(project: Project) {
        coreExtension(project)
    }

    private fun coreExtension(project: Project): CoreExtension = project.extensions
        .create(CoreExtension.PLUGIN_NAME, CoreExtension::class.java, project)
}
