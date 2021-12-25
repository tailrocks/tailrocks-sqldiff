import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension

plugins {
    `java-library`
    id("io.spring.dependency-management")
}

the<DependencyManagementExtension>().apply {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

description = "Starter for using Krendel"

dependencies {
    api(project(":krendel-core"))
    api(project(":krendel-spring-boot-autoconfigure"))
}
