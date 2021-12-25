plugins {
    `java-library`
}

description = "tailrocks-sqldiff Micronaut"

dependencies {
    // subprojects
    api(project(":tailrocks-sqldiff-hibernate"))

    // Micronaut
    annotationProcessor(platform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    annotationProcessor("io.micronaut:micronaut-inject-java")
    implementation(platform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.flyway:micronaut-flyway")

    // TODO remove me later, after this PR will be merged: https://github.com/micronaut-projects/micronaut-sql/pull/279
    implementation("io.micronaut.sql:micronaut-hibernate-jpa:3.0.1.BUILD-SNAPSHOT")

    // libraries
    implementation("javax.annotation:javax.annotation-api")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf(
            "-parameters",
            // enables incremental compilation
            "-Amicronaut.processing.incremental=true",
            "-Amicronaut.processing.annotations=com.scentbird.krendel.*",
            "-Amicronaut.processing.group=${project.group}",
            "-Amicronaut.processing.module=${project.name}"
    ))
}
