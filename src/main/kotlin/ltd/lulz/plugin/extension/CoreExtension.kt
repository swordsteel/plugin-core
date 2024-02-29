package ltd.lulz.plugin.extension

import java.time.OffsetDateTime.now
import java.time.ZoneId.of
import java.time.format.DateTimeFormatter.ofPattern
import org.gradle.api.Project

@Suppress("UNUSED_PARAMETER")
abstract class CoreExtension(project: Project) {

    companion object {
        const val COMPANY = "Lulz Ltd"
        const val PLUGIN_NAME = "core"
    }

    val timestamp = getTimeStamp()
    val vendor = COMPANY

    private fun getTimeStamp(): String = now()
        .atZoneSameInstant(of("UTC"))
        .format(ofPattern("yyyy-MM-dd HH:mm:ss z"))
        .toString()
}
