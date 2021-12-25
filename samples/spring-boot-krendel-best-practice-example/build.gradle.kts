subprojects {
    apply(plugin = "io.spring.dependency-management")

    repositories {
        mavenLocal()
        jcenter()
        mavenCentral()
        // TODO switch to use Maven Central or Bintray
        maven { url = uri("https://artifactory.scentbird.com/artifactory/scentbird-oss-releases") }
    }

    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
        }
    }
}
