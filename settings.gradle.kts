apply(from = File(settingsDir, "gradle/repositoriesSettings.gradle.kts"))

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
    ":samples:micronaut-best-practice:data-sqldiff",
    ":samples:micronaut-best-practice:web-app",
    ":samples:micronaut-simple",
    ":samples:spring-boot-best-practice:data",
    ":samples:spring-boot-best-practice:data-sqldiff",
    ":samples:spring-boot-best-practice:web-app",
    ":samples:spring-boot-simple",

    // FIXME remove me from here
    ":jambalaya-hibernate",
)
