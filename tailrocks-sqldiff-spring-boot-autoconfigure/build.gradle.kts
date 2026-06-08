plugins {
    id("maven-publish-conventions")
    id("spring-conventions")
}

description = "tailrocks-sqldiff AutoConfigure"

dependencies {
    api(project(":tailrocks-sqldiff-hibernate"))

    api("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework:spring-jdbc")
    implementation("org.springframework:spring-orm")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation(sqldiffLibs.hibernate.core)

    compileOnly("org.flywaydb:flyway-core")

    implementation(sqldiffLibs.slf4j.api)
}
