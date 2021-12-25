plugins {
    id("maven-publish-conventions")
}

// FIXME remove hurma

version = "0.1.0"

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api(sqldiffLibs.hibernate.core)
    api(sqldiffLibs.commons.lang)
    api(sqldiffLibs.classgraph)
}
