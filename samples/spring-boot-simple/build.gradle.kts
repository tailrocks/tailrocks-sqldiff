plugins {
    id("io.spring.dependency-management")
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":tailrocks-sqldiff-spring-boot-starter"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.postgresql:postgresql")
    runtimeOnly(sqldiffLibs.flyway.database.postgresql)

    implementation(platform(sqldiffLibs.boms.testcontainers))
    implementation("org.testcontainers:postgresql")
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines = setOf("junit-jupiter")
        excludeEngines = setOf("junit-vintage")
    }
}
