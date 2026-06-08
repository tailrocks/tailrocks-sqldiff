plugins {
    `java-library`
}

dependencies {
    annotationProcessor(platform(sqldiffLibs.boms.micronaut))
    annotationProcessor("io.micronaut:micronaut-inject-java")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    implementation(platform(sqldiffLibs.boms.micronaut))
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")

    api(sqldiffLibs.jakarta.annotation)
    api("ch.qos.logback:logback-classic")
    api("org.postgresql:postgresql")

    api(sqldiffLibs.hypersistence.utils)

    // FIXME remove me pls
    api(project(":jambalaya-hibernate"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(
        listOf(
            "-parameters",
            "-Amicronaut.processing.incremental=true",
            "-Amicronaut.processing.annotations=sqldiff.*,com.tailrocks.sqldiff.*",
            "-Amicronaut.processing.group=${project.group}",
            "-Amicronaut.processing.module=${project.name}"
        )
    )
}
