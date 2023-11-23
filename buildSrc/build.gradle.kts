plugins {
    `kotlin-dsl`
}

dependencies {
    // https://plugins.gradle.org/plugin/com.github.ben-manes.versions
    implementation("com.github.ben-manes:gradle-versions-plugin:0.43.0")
    // https://plugins.gradle.org/plugin/io.spring.dependency-management
    implementation("io.spring.gradle:dependency-management-plugin:1.1.0")
    // https://plugins.gradle.org/plugin/org.springframework.boot
    implementation("org.springframework.boot:spring-boot-gradle-plugin:2.7.4")
    // https://plugins.gradle.org/plugin/org.jetbrains.kotlin.jvm
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
}
