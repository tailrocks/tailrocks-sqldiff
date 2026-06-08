plugins {
    jacoco
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.withType<JacocoReport>().configureEach {
    executionData.setFrom(
        layout.buildDirectory.dir("jacoco").map { dir ->
            dir.asFileTree.matching { include("*.exec") }
        }
    )
    reports {
        xml.required.set(true)
    }
}
