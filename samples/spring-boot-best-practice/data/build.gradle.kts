plugins {
    `java-library`
}

dependencies {
    // Spring Boot
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-validation")

    // Flyway
    api("org.flywaydb:flyway-core")

    // PostgreSQL
    api("org.postgresql:postgresql")

    // Hibernate Types
    api(sqldiffLibs.hibernate.types)

    // FIXME remove me pls
    api(project(":jambalaya-hibernate"))
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
