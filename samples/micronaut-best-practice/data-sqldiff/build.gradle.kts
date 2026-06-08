plugins {
    application
}

dependencies {
    implementation(project(":tailrocks-sqldiff-micronaut"))
    implementation(project(":samples:micronaut-best-practice:data"))

    implementation(platform(sqldiffLibs.boms.testcontainers))
    implementation("org.testcontainers:postgresql")

    annotationProcessor(platform(sqldiffLibs.boms.micronaut))
    annotationProcessor("io.micronaut:micronaut-inject-java")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    implementation(platform(sqldiffLibs.boms.micronaut))
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut.validation:micronaut-validation")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    implementation("io.micronaut.flyway:micronaut-flyway")

    testAnnotationProcessor(platform(sqldiffLibs.boms.micronaut))
    testAnnotationProcessor("io.micronaut:micronaut-inject-java")
    testImplementation(platform(sqldiffLibs.boms.micronaut))
    testImplementation("io.micronaut.test:micronaut-test-junit5")

    implementation(sqldiffLibs.jakarta.annotation)
}

application {
    mainClass.set("sqldiff.example.micronaut.advanced.data.SqlDiffDataApplication")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf(
            "-parameters",
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
