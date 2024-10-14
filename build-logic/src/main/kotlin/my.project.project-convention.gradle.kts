import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}

version = rootProject.version

val javaTargetVersion = JavaVersion.VERSION_11.toString()

// Name JARs using full project name, based on the project path.
val projectJarName by extra(
    "${project.path}.jar"
        .substring(1) // Delete the leading ':',
        .replace(':', '.') // Replace the remaining ':' with '.'
)

tasks {

    // Use the specific JAR task names (e.g. jar, javadocJar, etc.) instead of
    // withType<Jar> to prevent using the same file names and overwriting JAR
    // files when other JAR tasks are executed.
    named<Jar>("jar").configure {
        archiveFileName.set(projectJarName)
    }

    withType<JavaCompile>().configureEach {

        with(options) {
            encoding = "UTF-8"

            release = javaTargetVersion.toInt()

            isFork = true
            forkOptions.memoryMaximumSize = "1g"
        }
    }

    withType<JavaExec>().configureEach {

        javaLauncher = javaToolchains.launcherFor {
            languageVersion = JavaLanguageVersion.of(javaTargetVersion)
        }
    }

    withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.fromTarget(javaTargetVersion)
            optIn = listOf(
                "kotlin.contracts.ExperimentalContracts",
                "kotlin.ExperimentalUnsignedTypes",
            )
            // Planned for deprecation:
            // https://youtrack.jetbrains.com/issue/KT-61035/
            freeCompilerArgs = listOf(
                // https://youtrack.jetbrains.com/issue/KT-61410/
                "-Xjsr305=strict",
                // https://youtrack.jetbrains.com/issue/KT-49746/
                "-Xjdk-release=$javaTargetVersion",
            )
        }
    }

    withType<Tar>().configureEach {
        enabled = false
    }
}
