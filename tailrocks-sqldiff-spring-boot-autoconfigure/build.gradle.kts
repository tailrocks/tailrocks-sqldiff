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

description = "Krendel AutoConfigure"

dependencies {
    api(project(":tailrocks-sqldiff-hibernate"))

    api("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework:spring-jdbc")
    implementation("org.springframework:spring-orm")
    implementation("jakarta.persistence:jakarta.persistence-api")
    implementation("org.hibernate:hibernate-core")

    compileOnly("org.flywaydb:flyway-core")

    implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
}
