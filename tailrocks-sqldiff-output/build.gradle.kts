plugins {
    id("maven-publish-conventions")
}

dependencies {
    api(project(":tailrocks-sqldiff-core"))
    api(project(":tailrocks-sqldiff-model"))
    api("io.projectreactor:reactor-core:3.3.4.RELEASE")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.0")

    api("org.fusesource.jansi:jansi:1.18")
}
