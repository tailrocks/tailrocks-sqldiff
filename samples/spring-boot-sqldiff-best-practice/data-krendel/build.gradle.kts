plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":tailrocks-sqldiff-spring-boot-starter"))

    // Import the data module with all JPA entities.
    implementation(project(":samples:spring-boot-krendel-best-practice-example:data"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation(platform("org.testcontainers:testcontainers-bom:${Versions.testcontainers}"))
    implementation("org.testcontainers:postgresql")

    // JUnit
    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")
}
