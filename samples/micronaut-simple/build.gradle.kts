import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    id("com.gradleup.shadow")
}

dependencies {
    implementation(project(":tailrocks-sqldiff-micronaut"))

    annotationProcessor(platform(sqldiffLibs.boms.micronaut))
    annotationProcessor("io.micronaut:micronaut-inject-java")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    implementation(platform(sqldiffLibs.boms.micronaut))
    implementation("io.micronaut:micronaut-inject")
    implementation("io.micronaut:micronaut-runtime")
    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")

    runtimeOnly("ch.qos.logback:logback-classic")
}

application {
    mainClass.set("sqldiff.micronaut.simple.sample.SimpleApplication")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
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
