pluginManagement {
    repositories {
        mavenLocal()
        maven(url = "https://maven.pkg.jetbrains.space/kotlin/p/kotlinx/maven")
        mavenCentral()
        gradlePluginPortal()
    }
    val dokkaVersion: String by settings
    val benchmarksVersion: String by settings
    plugins {
        id("org.jetbrains.dokka") version dokkaVersion
        id("me.champeau.jmh") version benchmarksVersion
    }
}

rootProject.name = "Kotlin-DateTime-library"

include(":core")
project(":core").name = "kotlinx-datetime"
include(":serialization")
project(":serialization").name = "kotlinx-datetime-serialization"
include(":benchmarks")

