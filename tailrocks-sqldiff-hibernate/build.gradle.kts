plugins {
    `java-library`
}

description = "Krendel Embedded"

dependencies {
    api(project(":krendel-output"))
    api(project(":krendel-flyway"))

    implementation("org.hibernate:hibernate-core:${Versions.hibernate}")
}
