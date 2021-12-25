plugins {
    `java-library`
}

description = "Krendel Embedded"

dependencies {
    api(project(":tailrocks-sqldiff-output"))
    api(project(":tailrocks-sqldiff-flyway"))

    implementation("org.hibernate:hibernate-core:${Versions.hibernate}")
}
