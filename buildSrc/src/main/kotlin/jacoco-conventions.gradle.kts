plugins {
    jacoco
}

jacoco {
    // https://search.maven.org/artifact/org.jacoco/jacoco
    toolVersion = "0.8.7"
}

tasks.withType<JacocoReport> {
    executionData.setFrom(fileTree(buildDir).include("/jacoco/*.exec"))
    reports {
        xml.required.set(true)
    }
}
