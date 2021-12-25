plugins {
    id("com.adarshr.test-logger")
    id("maven-publish-conventions")
    kotlin("jvm")
}

dependencies {
    api(project(":tailrocks-sqldiff-model"))

    // Kotlin
    testImplementation(kotlin("stdlib-jdk8"))
    testImplementation(kotlin("test-junit5"))

    api("org.jetbrains:annotations:${Versions.jetBrainsAnnotations}")
    api("org.postgresql:postgresql:${Versions.postgresql}")
    implementation("org.apache.commons:commons-collections4:${Versions.commonsCollections}")
    api("org.apache.commons:commons-lang3:${Versions.commonsLang}")

    // Testing
    testImplementation("org.testcontainers:postgresql:${Versions.testcontainers}")
    testImplementation("org.testcontainers:junit-jupiter:${Versions.testcontainers}")
}
