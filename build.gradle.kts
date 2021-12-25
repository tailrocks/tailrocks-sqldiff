plugins {
    java
    idea
    `maven-publish`
    id("com.adarshr.test-logger") version Versions.gradleTestLoggerPlugin apply false
    id("com.github.johnrengelman.shadow") version Versions.gradleShadowPlugin apply false
}

allprojects {
    apply(plugin = "idea-conventions")
    apply(plugin = "versions-conventions")
}

subprojects {
    apply(plugin = "java-conventions")
    apply(plugin = "junit-conventions")
    apply(plugin = "jacoco-conventions")

    dependencies {
        // Logs
        implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
        implementation("ch.qos.logback:logback-classic:${Versions.logback}")

        // JUnit
        testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")
    }
}
