import java.lang.System.getenv

dependencyResolutionManagement {

    fun getProperty(property: String): String = extra[property] as String

    fun retrieveConfiguration(
        property: String,
        environment: String,
    ): String? = if (extra.has(property)) getProperty(property) else getenv(environment)

    @Suppress("UnstableApiUsage")
    repositories {
        mavenLocal()
        // TODO configuration for stored version catalogs.
        // maven {
        //     name = "LuLz-Ltd"
        //     url = uri("https://")
        //     credentials(HttpHeaderCredentials::class) {
        //         username = retrieveConfiguration("repositoryUser", "REPOSITORY_USER")
        //         password = retrieveConfiguration("repositoryToken", "REPOSITORY_TOKEN")
        //     }
        //     authentication {
        //         create("header", HttpHeaderAuthentication::class)
        //     }
        // }
    }
    versionCatalogs.create("lulz").from("ltd.lulz.catalog:lulz-version:${getProperty("catalog")}")
}

rootProject.name = "core"
