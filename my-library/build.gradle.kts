plugins {
    id("my.project.project-convention")
}

description = "This module represents a library that we want to obfuscate."

dependencies {
    implementation(libs.log4j.kotlin)
}
