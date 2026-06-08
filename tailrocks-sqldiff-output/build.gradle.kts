plugins {
    id("maven-publish-conventions")
}

dependencies {
    api(project(":tailrocks-sqldiff-core"))
    api(project(":tailrocks-sqldiff-model"))
    api(sqldiffLibs.reactor.core)
    api(sqldiffLibs.jackson.dataformat.yaml)

    api(sqldiffLibs.jansi)
}
