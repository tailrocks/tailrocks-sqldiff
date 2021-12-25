plugins {
    `java-library`
}

description = "tailrocks-sqldiff Embedded"

dependencies {
    api(project(":tailrocks-sqldiff-output"))
    api(project(":tailrocks-sqldiff-flyway"))

    implementation("org.hibernate:hibernate-core:${Versions.hibernate}")
}
