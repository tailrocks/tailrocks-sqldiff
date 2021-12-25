subprojects {
    repositories {
        mavenLocal()
        jcenter()
        mavenCentral()
        // TODO switch to use Maven Central or Bintray
        maven { url = uri("https://artifactory.scentbird.com/artifactory/scentbird-oss-releases") }
    }
}
