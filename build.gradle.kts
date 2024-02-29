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
    id("io.gitlab.arturbosch.detekt") version "1.23.5"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"

    kotlin("jvm") version "1.9.22"

    `java-gradle-plugin`
    `maven-publish`
}

dependencies {
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
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
