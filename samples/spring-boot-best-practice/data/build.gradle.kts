plugins {
    `java-library`
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-validation")

    api("org.flywaydb:flyway-core")
    runtimeOnly(sqldiffLibs.flyway.database.postgresql)

    api("org.postgresql:postgresql")

    api(sqldiffLibs.hypersistence.utils)

    // FIXME remove me pls
    api(project(":jambalaya-hibernate"))
}

tasks.getByName<Jar>("jar") {
    enabled = true
}
