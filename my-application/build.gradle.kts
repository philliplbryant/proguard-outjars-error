plugins {
    id("my.project.project-convention")
    id("application")
    id("my.release.proguard-convention")
}

description = "This module represents an application to be obfuscated using " +
        "the ProGuard Gradle plugin."

version = rootProject.version

dependencies {
    implementation(projects.myLibrary)
}

application {
    mainClass.set("my.application.MyApplicationKt")
    applicationDefaultJvmArgs += listOf(
        "-Xms8m",
        "-Xmx64m",
    )
}
