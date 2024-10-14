plugins {
    // Not using `id("kotlin-dsl")` syntax per
    // https://github.com/gradle/gradle/issues/23884.
    `kotlin-dsl`
}

dependencies {
    implementation(gradleApi())
    implementation(libs.kotlin.gradle.plugin.dependency)
    implementation(libs.proguard.gradle.plugin.dependency)
}
