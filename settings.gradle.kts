rootProject.name = "tailrocks-sqldiff"

include(
    ":tailrocks-sqldiff-cli",
    ":tailrocks-sqldiff-core",
    ":tailrocks-sqldiff-flyway",
    ":tailrocks-sqldiff-hibernate",
    ":tailrocks-sqldiff-micronaut",
    ":tailrocks-sqldiff-model",
    ":tailrocks-sqldiff-output",
    ":tailrocks-sqldiff-spring-boot-autoconfigure",
    ":tailrocks-sqldiff-spring-boot-starter",
    ":samples:micronaut-sqldiff-best-practice:data",
    ":samples:micronaut-sqldiff-best-practice:data-krendel",
    ":samples:micronaut-sqldiff-best-practice:web-app",
    ":samples:micronaut-sqldiff-simple",
    ":samples:spring-boot-sqldiff-best-practice:data",
    ":samples:spring-boot-sqldiff-best-practice:data-krendel",
    ":samples:spring-boot-sqldiff-best-practice:web-app",
    ":samples:spring-boot-sqldiff-simple",
)
