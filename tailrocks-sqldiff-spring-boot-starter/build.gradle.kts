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

description = "Starter for using tailrocks-sqldiff"

dependencies {
    api(project(":tailrocks-sqldiff-core"))
    api(project(":tailrocks-sqldiff-spring-boot-autoconfigure"))
}
