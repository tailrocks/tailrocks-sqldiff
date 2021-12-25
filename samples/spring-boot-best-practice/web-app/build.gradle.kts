plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":samples:spring-boot-best-practice:data"))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
