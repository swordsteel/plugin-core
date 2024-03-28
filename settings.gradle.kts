dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenLocal()
        // TODO configuration for stored version catalogs.
        // maven {
        //     url = uri("https://")
        //     credentials {
        //         username =
        //         password =
        //     }
        // }
    }
    val catalog: String by settings
    versionCatalogs.create("lulz").from("ltd.lulz.catalog:lulz-version:$catalog")
}

rootProject.name = "core"
