plugins {
    application
}

dependencies {
    implementation(project(":tailrocks-sqldiff-micronaut"))

    // Import the data module with all JPA entities.
    implementation(project(":samples:micronaut-best-practice:data"))

    // Testcontainers
    implementation(platform("org.testcontainers:testcontainers-bom:${Versions.testcontainers}"))
    implementation("org.testcontainers:postgresql")

    // Micronaut
    annotationProcessor(platform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    annotationProcessor("io.micronaut:micronaut-inject-java")
    annotationProcessor("io.micronaut:micronaut-validation")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    implementation(platform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-validation")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.flyway:micronaut-flyway")

    // TODO remove me later, after this PR will be merged: https://github.com/micronaut-projects/micronaut-sql/pull/279
    implementation("io.micronaut.sql:micronaut-hibernate-jpa:4.8.1")

    testAnnotationProcessor(platform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    testAnnotationProcessor("io.micronaut:micronaut-inject-java")
    testImplementation(platform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    testImplementation("io.micronaut.test:micronaut-test-junit5")

    // libraries
    implementation("javax.annotation:javax.annotation-api")

    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")
}

application {
    mainClassName = "sqldiff.example.micronaut.advanced.data.SqlDiffDataApplication"
    mainClass.set("sqldiff.example.micronaut.advanced.data.SqlDiffDataApplication")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf(
            "-parameters",
            // enables incremental compilation
            "-Amicronaut.processing.incremental=true",
            "-Amicronaut.processing.annotations=sqldiff.*,com.tailrocks.sqldiff.*",
            "-Amicronaut.processing.group=${project.group}",
            "-Amicronaut.processing.module=${project.name}"
    ))
}

tasks.withType<JavaExec> {
    jvmArgs("-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote")
    if (gradle.startParameter.isContinuous) {
        systemProperties(mapOf(
                "micronaut.io.watch.restart" to "true",
                "micronaut.io.watch.enabled" to "true",
                "micronaut.io.watch.paths" to "src/main"
        ))
    }
}
