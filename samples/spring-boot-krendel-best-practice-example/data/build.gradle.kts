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

    // Hurma
    api("com.scentbird.hurma:hurma-hibernate:${Versions.scentbirdHurmaHibernate}")
    api("com.scentbird.hurma:hurma-spring-data-jpa:${Versions.scentbirdHurmaSpringDataJpa}")
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
