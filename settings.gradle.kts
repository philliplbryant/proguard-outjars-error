/**
 * This file is required for Gradle multi-project builds.  We need to list each
 * module in our multi-project build here.
 */

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }

    includeBuild("build-logic")
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

rootProject.name = "proguard-outjars-error"

// Feature flag implementing strictness during stabilization of configuration
// caching. Gradle documentation recommends enabling it to prepare for flag
// removal and making the linked features the default.  See also
// https://docs.gradle.org/current/userguide/configuration_cache.html
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

// Feature flag that allows declaring project dependencies using type-safe
// accessors such as "api(projects.apiV1.apiJreDomainGeo)" in lieu of
// "api(project(":api-v1:api-jre-domain-geo"))"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include("my-application")
include("my-library")
