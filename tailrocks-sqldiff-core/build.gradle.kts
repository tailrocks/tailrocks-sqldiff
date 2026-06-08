plugins {
    id("com.adarshr.test-logger")
    id("maven-publish-conventions")
    kotlin("jvm")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    api(project(":tailrocks-sqldiff-model"))

    // Kotlin
    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit5"))

    api(sqldiffLibs.jetbrains.annotations)
    api(sqldiffLibs.postgresql)
    api(sqldiffLibs.commons.collections)
    api(sqldiffLibs.commons.lang)

    // Testcontainers
    testImplementation(platform(sqldiffLibs.boms.testcontainers))
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
}
