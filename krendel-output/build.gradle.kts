plugins {
    `java-library`
}

dependencies {
    api(project(":krendel-core"))
    api(project(":krendel-model"))
    api("io.projectreactor:reactor-core:3.3.4.RELEASE")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.11.0")

    api("org.fusesource.jansi:jansi:1.18")
}
