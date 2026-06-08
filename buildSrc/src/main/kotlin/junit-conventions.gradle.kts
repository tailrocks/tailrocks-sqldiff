val isFailFast = System.getenv("GRADLE_FAIL_FAST") == null ||
        System.getenv("GRADLE_FAIL_FAST").lowercase() == "true"

val isParallel = System.getenv("GRADLE_JUNIT_PARALLEL") == "true"

tasks.withType<Test> {
    useJUnitPlatform()
    failFast = isFailFast
    if (isParallel) {
        systemProperties["junit.jupiter.execution.parallel.enabled"] = true
        systemProperties["junit.jupiter.execution.parallel.mode.default"] = "concurrent"
        systemProperties["junit.jupiter.execution.parallel.mode.classes.default"] = "concurrent"
    }
}
