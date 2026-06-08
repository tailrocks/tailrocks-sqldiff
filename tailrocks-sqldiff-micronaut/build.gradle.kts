plugins {
    id("maven-publish-conventions")
}

description = "tailrocks-sqldiff Micronaut"

dependencies {
    api(project(":tailrocks-sqldiff-hibernate"))

    annotationProcessor(platform(sqldiffLibs.boms.micronaut))
    annotationProcessor("io.micronaut:micronaut-inject-java")
    implementation(platform(sqldiffLibs.boms.micronaut))
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.flyway:micronaut-flyway")

    implementation(sqldiffLibs.jakarta.annotation)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(
        listOf(
            "-parameters",
            "-Amicronaut.processing.incremental=true",
            "-Amicronaut.processing.annotations=com.tailrocks.sqldiff.*",
            "-Amicronaut.processing.group=${project.group}",
            "-Amicronaut.processing.module=${project.name}"
        )
    )
}
