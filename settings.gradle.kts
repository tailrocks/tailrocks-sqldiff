apply(from = File(settingsDir, "gradle/repositoriesSettings.gradle.kts"))

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    versionCatalogs {
        create("sqldiffLibs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "tailrocks-sqldiff"

include(
    ":tailrocks-sqldiff-cli",
    ":tailrocks-sqldiff-core",
    ":tailrocks-sqldiff-flyway",
    ":tailrocks-sqldiff-hibernate",
    ":tailrocks-sqldiff-micronaut",
    ":tailrocks-sqldiff-model",
    ":tailrocks-sqldiff-output",
    ":tailrocks-sqldiff-spring-boot-autoconfigure",
    ":tailrocks-sqldiff-spring-boot-starter",

    ":samples:micronaut-best-practice:data",
    ":samples:micronaut-best-practice:data-krendel",
    ":samples:micronaut-best-practice:web-app",
    ":samples:micronaut-simple",
    ":samples:spring-boot-best-practice:data",
    ":samples:spring-boot-best-practice:data-krendel",
    ":samples:spring-boot-best-practice:web-app",
    ":samples:spring-boot-simple",
)
