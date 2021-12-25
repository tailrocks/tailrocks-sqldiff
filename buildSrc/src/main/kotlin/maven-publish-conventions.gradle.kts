plugins {
    `java-library`
    `maven-publish`
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            versionMapping {
                allVariants {
                    fromResolutionResult()
                }
            }
            pom {
                // TODO temp fix: https://github.com/gradle/gradle/issues/10861
                withXml {
                    val root = asNode()
                    var nodes = root["dependencyManagement"] as groovy.util.NodeList
                    while (nodes.isNotEmpty()) {
                        root.remove(nodes.first() as groovy.util.Node)

                        nodes = root["dependencyManagement"] as groovy.util.NodeList
                    }
                }
                // @end temp fix
            }
        }
    }
    repositories {
        maven {
            name = "Artifactory"
            setUrl("${System.getenv("ARTIFACTORY_CONTEXT_URL")}/${System.getenv("ARTIFACTORY_RELEASE_REPO_KEY")}")
            credentials {
                username = System.getenv("ARTIFACTORY_USERNAME") ?: return@credentials
                password = System.getenv("ARTIFACTORY_PASSWORD") ?: return@credentials
            }
        }
    }
}
