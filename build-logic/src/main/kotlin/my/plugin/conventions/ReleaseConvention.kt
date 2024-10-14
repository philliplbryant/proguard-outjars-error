package my.plugin.conventions

import my.plugin.conventions.ApplicationConvention.getDelimitedText
import my.plugin.conventions.ApplicationConvention.isMyJar
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.provideDelegate

class ReleaseInformationFactory(
    private val project: Project,
) {

    val debuggingDir: Provider<Directory> =
        project.layout.buildDirectory.dir("debugging")

    @Suppress("MemberVisibilityCanBePrivate")
    val releaseDir: Provider<Directory> =
        project.layout.buildDirectory.dir("release")

    val obfuscatedJarsDir: Provider<Directory> by lazy {
        releaseDir.map {
            it.dir("obfuscated")
        }
    }

    /**
     * Returns a list of unobfuscated files for the main distribution.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    val unobfuscatedLibraryFiles: Provider<String> by lazy {

        project.provider {

            // Third-party runtime dependencies. Dependencies that are not
            // third-party must be obfuscated.
            val files = project.configurations.getByName("runtimeClasspath")
                .asFileTree.files.filter { file ->
                    !isMyJar(file)
                }

            getDelimitedText(
                delimiter = ";",
                relativePathToFile = null,
                dependencyFiles = files,
            )
        }
    }
}
