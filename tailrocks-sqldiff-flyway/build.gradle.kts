plugins {
    `java-library`
}

dependencies {
    api(project(":krendel-core"))
    api(project(":krendel-output"))

    // Flyway
    compileOnly("org.flywaydb:flyway-core:${Versions.flyway}")

    implementation("com.github.jsqlparser:jsqlparser:3.1")

    // TODO replace with https://github.com/apache/commons-csv
    api("com.opencsv:opencsv:5.2")
}
