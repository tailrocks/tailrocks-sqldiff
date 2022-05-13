plugins {
    `java-library`
}

dependencies {
    // Micronaut
    annotationProcessor(platform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    annotationProcessor("io.micronaut:micronaut-inject-java")
    annotationProcessor("io.micronaut:micronaut-validation")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    implementation(platform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")

    // TODO remove me later, after this PR will be merged: https://github.com/micronaut-projects/micronaut-sql/pull/279
    api("io.micronaut.sql:micronaut-hibernate-jpa:4.4.0")

    api("javax.annotation:javax.annotation-api")
    api("ch.qos.logback:logback-classic")
    api("org.postgresql:postgresql")

    // Hibernate Types
    api(sqldiffLibs.hibernate.types)

    // FIXME remove me pls
    api(project(":jambalaya-hibernate"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(
        listOf(
            "-parameters",
            // enables incremental compilation
            "-Amicronaut.processing.incremental=true",
            "-Amicronaut.processing.annotations=sqldiff.*,com.tailrocks.sqldiff.*",
            "-Amicronaut.processing.group=${project.group}",
            "-Amicronaut.processing.module=${project.name}"
        )
    )
}
