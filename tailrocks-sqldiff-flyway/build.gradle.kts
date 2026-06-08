plugins {
    id("maven-publish-conventions")
}

dependencies {
    api(project(":tailrocks-sqldiff-core"))
    api(project(":tailrocks-sqldiff-output"))

    compileOnly(sqldiffLibs.flyway.core)

    implementation(sqldiffLibs.jsqlparser)

    // TODO replace with https://github.com/apache/commons-csv
    api(sqldiffLibs.opencsv)
}
