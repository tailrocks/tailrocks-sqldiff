import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.gradleup.shadow")
    application
}

version = "0.1"
group = "com.tailrocks.sqldiff.cli"

dependencies {
    // subprojects
    implementation(project(":tailrocks-sqldiff-output"))
    implementation(project(":tailrocks-sqldiff-flyway"))

    annotationProcessor(platform(sqldiffLibs.boms.micronaut))
    annotationProcessor("io.micronaut:micronaut-inject-java")
    annotationProcessor("info.picocli:picocli-codegen:${Versions.picocli}")
    implementation(platform(sqldiffLibs.boms.micronaut))
    implementation("info.picocli:picocli")
    implementation("io.micronaut.picocli:micronaut-picocli")
    runtimeOnly("ch.qos.logback:logback-classic")
    implementation("org.postgresql:postgresql") {
        version {
            strictly(Versions.postgresql)
        }
    }
}

application {
    mainClass.set("com.tailrocks.sqldiff.cli.command.SqlDiffCommand")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(
        listOf(
            "-parameters",
            "-Amicronaut.processing.incremental=true",
            "-Amicronaut.processing.annotations=com.scentbird.krendel.cli.*",
            "-Amicronaut.processing.group=${project.group}",
            "-Amicronaut.processing.module=${project.name}"
        )
    )
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}

tasks.withType<JavaExec> {
    jvmArgs("-XX:TieredStopAtLevel=1", "-Dcom.sun.management.jmxremote")
    if (gradle.startParameter.isContinuous) {
        systemProperties(
            mapOf(
                "micronaut.io.watch.restart" to "true",
                "micronaut.io.watch.enabled" to "true",
                "micronaut.io.watch.paths" to "src/main"
            )
        )
    }
}
