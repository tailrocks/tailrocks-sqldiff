plugins {
    id("maven-publish-conventions")
}

dependencies {
    api(project(":tailrocks-sqldiff-core"))
    api(project(":tailrocks-sqldiff-output"))

    // Flyway
    compileOnly(sqldiffLibs.flyway.core)

    implementation("com.github.jsqlparser:jsqlparser:4.5")

    // TODO replace with https://github.com/apache/commons-csv
    api("com.opencsv:opencsv:5.7.0")
}
