plugins {
    id("maven-publish-conventions")
}

dependencies {
    api(project(":tailrocks-sqldiff-core"))
    api(project(":tailrocks-sqldiff-model"))
    api("io.projectreactor:reactor-core:3.4.17")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.2")

    api("org.fusesource.jansi:jansi:2.4.0")
}
