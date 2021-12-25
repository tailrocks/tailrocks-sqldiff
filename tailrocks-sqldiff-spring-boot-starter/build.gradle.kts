plugins {
    id("maven-publish-conventions")
    id("spring-conventions")
}

description = "Starter for using tailrocks-sqldiff"

dependencies {
    api(project(":tailrocks-sqldiff-core"))
    api(project(":tailrocks-sqldiff-spring-boot-autoconfigure"))
}
