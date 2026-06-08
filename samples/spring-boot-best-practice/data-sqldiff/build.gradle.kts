plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":tailrocks-sqldiff-spring-boot-starter"))
    implementation(project(":samples:spring-boot-best-practice:data"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation(platform(sqldiffLibs.boms.testcontainers))
    implementation("org.testcontainers:postgresql")
}
