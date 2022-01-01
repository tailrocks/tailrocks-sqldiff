plugins {
    id("maven-publish-conventions")
}

description = "tailrocks-sqldiff Embedded"

dependencies {
    api(project(":tailrocks-sqldiff-output"))
    api(project(":tailrocks-sqldiff-flyway"))

    implementation(sqldiffLibs.hibernate.core)
}
