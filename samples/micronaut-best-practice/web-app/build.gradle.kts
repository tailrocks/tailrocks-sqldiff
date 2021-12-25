import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":samples:micronaut-best-practice:data"))

    // Micronaut
    annotationProcessor(platform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    annotationProcessor("io.micronaut:micronaut-inject-java")
    annotationProcessor("io.micronaut:micronaut-validation")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    implementation(platform("io.micronaut:micronaut-bom:${Versions.micronaut}"))
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut:micronaut-http-client")
}


application {
    mainClassName = "krendel.example.micronaut.advanced.web.WebApplication"
    mainClass.set("krendel.example.micronaut.advanced.web.WebApplication")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf(
            "-parameters",
            // enables incremental compilation
            "-Amicronaut.processing.incremental=true",
            "-Amicronaut.processing.annotations=krendel.*,com.scentbird.krendel.*",
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
