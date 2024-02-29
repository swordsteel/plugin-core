package ltd.lulz.plugin.extension

import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import java.time.OffsetDateTime
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CoreExtensionTest {

    companion object {
        const val PLUGIN_ID = "ltd.lulz.plugin.core"
        const val SNAPSHOT_VERSION = "0.0.0-SNAPSHOT"
        const val EXTENSION = "core"

        const val CORE_TIMESTAMP = "2002-02-20 01:10:11 UTC"
        const val VENDOR = "Lulz Ltd"
        const val TIMESTAMP = "2002-02-20T02:10:11+01:00"

        @JvmStatic
        @BeforeAll
        fun buildUp() {
            mockkStatic(OffsetDateTime::class)
            every { OffsetDateTime.now() } returns OffsetDateTime.parse(TIMESTAMP)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            unmockkStatic(OffsetDateTime::class)
        }
    }

    lateinit var project: Project

    @BeforeEach
    fun beforeEach() {
        project = ProjectBuilder.builder().build()
        project.version = SNAPSHOT_VERSION
        project.pluginManager.apply(PLUGIN_ID)
    }

    @Test
    fun getVendor() {
        // when
        val extension = project.extensions.getByName(EXTENSION) as CoreExtension

        // then
        Assertions.assertEquals(VENDOR, extension.vendor)
    }

    @Test
    fun getTimestamp() {
        // when
        val extension = project.extensions.getByName(EXTENSION) as CoreExtension

        // then
        Assertions.assertEquals(CORE_TIMESTAMP, extension.timestamp)
    }
}
