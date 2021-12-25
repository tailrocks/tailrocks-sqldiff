import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath("org.postgresql:postgresql:${Versions.postgresql}")
    }
}

plugins {
    java
    idea
    `maven-publish`
    id("com.adarshr.test-logger") version Versions.gradleTestLoggerPlugin apply false
    id("net.rdrei.android.buildtimetracker") version Versions.gradleBuildTimeTrackerPlugin
    id("io.spring.dependency-management") version Versions.gradleSpringDependencyManagement apply true
    id("org.springframework.boot") version Versions.springBoot apply false
    id("com.jfrog.artifactory") version Versions.gradleArtifactoryPlugin apply false
    id("com.github.johnrengelman.shadow") version Versions.gradleShadowPlugin apply false
    kotlin("jvm") version Versions.kotlin apply false
}

buildtimetracker {
    reporters {
        register("summary") {
            options["ordered"] = "true"
            options["barstyle"] = "none"
            options["shortenTaskNames"] = "false"
        }
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "idea")
    apply(plugin = "net.rdrei.android.buildtimetracker")

    apply(from = "${project.rootDir}/gradle/dependencyUpdates.gradle.kts")

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    idea {
        module {
            isDownloadJavadoc = false
            isDownloadSources = false
        }
    }

    repositories {
        mavenLocal()
        jcenter()
        mavenCentral()
        maven { url = uri("https://artifactory.scentbird.com/artifactory/maven-public") }
        maven { url = uri("https://oss.jfrog.org/oss-snapshot-local") }
    }
}

val publishingProjects = setOf(
        "krendel-core",
        "krendel-flyway",
        "krendel-hibernate",
        "krendel-micronaut",
        "krendel-model",
        "krendel-output",
        "krendel-spring-boot-autoconfigure",
        "krendel-spring-boot-starter"
)

subprojects {
    apply(plugin = "java")
    if (publishingProjects.contains(project.name)) {
        apply(plugin = "maven-publish")
        apply(plugin = "com.jfrog.artifactory")
    }

    version = Versions.project
    group = "com.scentbird.krendel"

    java {
        withJavadocJar()
        withSourcesJar()
    }

    dependencies {
        // Logs
        implementation("org.slf4j:slf4j-api:${Versions.slf4j}")
        implementation("ch.qos.logback:logback-classic:${Versions.logback}")

        // JUnit
        testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")
    }

    if (publishingProjects.contains(project.name)) {
        publishing {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])
                    versionMapping {
                        allVariants {
                            fromResolutionResult()
                        }
                    }
                }
            }
        }

        the<org.jfrog.gradle.plugin.artifactory.dsl.ArtifactoryPluginConvention>().apply {
            setContextUrl(System.getenv("ARTIFACTORY_CONTEXT_URL"))

            publish(delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig> {
                defaults(delegateClosureOf<groovy.lang.GroovyObject> {
                    invokeMethod("publications", "mavenJava")
                    setProperty("publishArtifacts", true)
                    setProperty("publishPom", true)
                })
                repository(delegateClosureOf<groovy.lang.GroovyObject> {
                    var repoKey = System.getenv("ARTIFACTORY_RELEASE_REPO_KEY") ?: "plugins-release-local"

                    if (version.toString().contains("SNAPSHOT")) {
                        repoKey = System.getenv("ARTIFACTORY_SNAPSHOT_REPO_KEY") ?: "plugins-snapshot-local"
                    }

                    setProperty("repoKey", repoKey)
                    setProperty("username", System.getenv("ARTIFACTORY_USERNAME") ?: "admin")
                    setProperty("password", System.getenv("ARTIFACTORY_PASSWORD") ?: "password")
                    setProperty("publishBuildInfo", false)
                })
            })
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform {
            includeEngines = setOf("junit-jupiter")
            excludeEngines = setOf("junit-vintage")
        }
    }
}
