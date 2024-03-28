import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension.Companion.DEFAULT_SRC_DIR_KOTLIN
import io.gitlab.arturbosch.detekt.extensions.DetektExtension.Companion.DEFAULT_TEST_SRC_DIR_KOTLIN
import java.time.OffsetDateTime.now
import java.time.ZoneId.of
import java.time.format.DateTimeFormatter.ofPattern
import org.gradle.api.JavaVersion.VERSION_17
import org.gradle.kotlin.dsl.support.uppercaseFirstChar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType.SARIF

plugins {
    alias(lulz.plugins.io.gitlab.arturbosch.detekt)
    alias(lulz.plugins.org.jlleitschuh.gradle.ktlint)

    alias(lulz.plugins.kotlin.jvm)

    `java-gradle-plugin`
    `maven-publish`
}

dependencies {
    implementation(lulz.org.eclipse.jgit)

    testImplementation(lulz.io.mockk)
    testImplementation(lulz.org.junit.jupiter.api)
    testImplementation(lulz.org.junit.jupiter.params)

    testRuntimeOnly(lulz.org.junit.platform.launcher)
}

description = "Lulz Ltd Test Plugin Core"
group = "ltd.lulz.plugin"

val timestamp = now()
    .atZoneSameInstant(of("UTC"))
    .format(ofPattern("yyyy-MM-dd HH:mm:ss z"))
    .toString()

detekt {
    buildUponDefaultConfig = true
    basePath = projectDir.path
    source.from(DEFAULT_SRC_DIR_KOTLIN, DEFAULT_TEST_SRC_DIR_KOTLIN)
}

gradlePlugin {
    plugins.create("core") {
        id = "ltd.lulz.plugin.core"
        implementationClass = "ltd.lulz.plugin.CorePlugin"
    }
}

java {
    sourceCompatibility = VERSION_17
    targetCompatibility = VERSION_17
    withSourcesJar()
}

ktlint {
    verbose = true
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
    kotlinScriptAdditionalPaths {
        include(fileTree("scripts/*"))
    }
    reporters {
        reporter(SARIF)
    }
}

publishing {
    repositories {
        // TODO configuration for publishing packages
        // maven {
        //     url = uri("https://")
        //     credentials {
        //         username =
        //         password =
        //     }
        // }
        publications.register("mavenJava", MavenPublication::class) { from(components["java"]) }
    }
}

repositories {
    mavenCentral()
}

tasks {
    withType<Detekt> {
        reports {
            html.required = false
            md.required = false
            sarif.required = true
            txt.required = false
            xml.required = false
        }
    }
    withType<Jar> {
        manifest.attributes.apply {
            put("Implementation-Title", project.name.uppercaseFirstChar())
            put("Implementation-Version", project.version)
            put("Implementation-Vendor", "Lulz Ltd")
            put("Built-By", System.getProperty("user.name"))
            put("Built-Gradle", project.gradle.gradleVersion)
            put("Built-JDK", System.getProperty("java.version"))
            put("Built-OS", "${System.getProperty("os.name")} v${System.getProperty("os.version")}")
            put("Built-Time", timestamp)
        }
    }
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = "17"
        }
    }
    withType<Test> {
        useJUnitPlatform()
    }
}
