package my.plugin.conventions

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaApplication
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.get
import java.io.File

/**
 * Constants and extension functions for use by [Project]s applying the
 * application plugin.  [Project] extension functions are used to prevent
 * configuration resolution from a context different than the project context,
 * which has been deprecated.
 */
object ApplicationConvention {

    /**
     * The name of the distribution for Linux based operating systems.
     */
    const val LINUX_DISTRIBUTION_NAME = "linux"

    /**
     * The name of the distribution for Windows based operating systems.
     */
    const val WINDOWS_DISTRIBUTION_NAME = "windows"

    fun isMyJar(file: File): Boolean =
        file.name.lowercase().startsWith("my")

    /**
     * Returns a delimited list of the relative paths to the files to be added
     * to the class path, where the delimiter used is specific to the
     * distribution specified and the path is relative to the installation
     * location (lib directory).
     * @param project the [Project] for which the classpath is to be returned.
     * @param distributionName the name of the distribution for which the module
     * path is to be required.  Valid values include [LINUX_DISTRIBUTION_NAME]
     * and [WINDOWS_DISTRIBUTION_NAME].
     * @param additionalEntries additional entries to be added to the classpath.
     * This can be used for situations such as adding a directory containing
     * security related resources.
     * @return a delimited list of the relative paths to the files to be added
     * to the class path.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getClassPath(
        project: Project,
        distributionName: String,
        vararg additionalEntries: String,
    ): Provider<String> = project.providers.provider {

        // The relative path from the Install4J launchers to the folder
        // containing runtime dependencies
        val relativePathToDependencies = "lib"

        // FIXME: Do not depend on the other project's JAR task to add it to
        //  the classpath
        val jar = project.tasks.getByName("jar")

        // Get classpath dependencies - use a LinkedHashSet to keep the
        // application JAR at the beginning to allow for faster startup
        val commonClasspathFileSet = LinkedHashSet<File>()
        commonClasspathFileSet.addAll(jar.outputs.files.files)

        // Runtime classpath dependencies
        commonClasspathFileSet.addAll(
            project.configurations.getByName("runtimeClasspath")
                .filter { file ->
                    // "main" refers to the application's class
                    // files; include them by adding the application
                    // JAR below
                    file.name != "main"
                }.files
        )

        val classpathText = when (distributionName) {
            LINUX_DISTRIBUTION_NAME -> {
                // Linux specific and common classpath dependencies
                val linuxClasspathFileSet =
                    commonClasspathFileSet.toMutableSet()

                val delimiter = ":"

                val delimitedText = getDelimitedText(
                    delimiter = delimiter,
                    relativePathToFile = relativePathToDependencies,
                    dependencyFiles = linuxClasspathFileSet,
                )

                if (additionalEntries.isNotEmpty()) {
                    delimitedText +
                            delimiter +
                            additionalEntries.joinToString(delimiter)
                } else {
                    delimitedText
                }
            }

            WINDOWS_DISTRIBUTION_NAME -> {
                // Windows specific and common classpath dependencies
                val windowsClasspathFileSet =
                    commonClasspathFileSet.toMutableSet()

                val delimiter = ";"

                val delimitedText = getDelimitedText(
                    delimiter = delimiter,
                    relativePathToFile = relativePathToDependencies,
                    dependencyFiles = windowsClasspathFileSet,
                )

                if (additionalEntries.isNotEmpty()) {
                    delimitedText +
                            delimiter +
                            additionalEntries.joinToString(delimiter)
                } else {
                    delimitedText
                }
            }

            else -> ""
        }

        classpathText
    }

    /**
     * Returns a space delimited list of the [Project]'s
     * applicationDefaultJvmArgs as specified in the [JavaApplication]
     * extension.  This is intended for use when passing the JVM args to an
     * Install4J configuration as a compiler variable and/or for writing the
     * JVM args to an argument file for use when running the application from an
     * IntelliJ run configuration.
     * @param project the [Project] for which the default JVM arguments are to
     * be returned.
     * @return the [Project]'s applicationDefaultJvmArgs.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getDefaultJvmArgs(project: Project): Provider<String> =
        project.providers.provider {

            val application = getJavaApplication(project)
            application.applicationDefaultJvmArgs.joinToString(" ")
        }

    /**
     * Returns the [Project]'s main class as specified in the [JavaApplication]
     * extension.  This function is intended for use when passing main class to
     * an Install4J configuration as a compiler variable.
     * @param project the [Project] for which the main class is to be returned.
     * @return the [Project]'s main class.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    fun getMainClass(project: Project): Provider<String> =
        project.providers.provider {

            val application = getJavaApplication(project)
            application.mainClass.get()
        }

    private fun getJavaApplication(project: Project): JavaApplication {
        project.plugins.findPlugin(ApplicationPlugin::class.java)
            ?: throw GradleException(
                "The ApplicationPlugin must be applied to the project calling" +
                        " this function."
            )

        return project.extensions["application"] as JavaApplication
    }

    /**
     * Returns a delimited list of dependency files, including the paths to the
     * files, where the paths are absolute when [relativePathToFile] is null or
     * empty and relative when [relativePathToFile] is not null and not empty.
     */
    fun getDelimitedText(
        delimiter: String,
        relativePathToFile: String?,
        dependencyFiles: Iterable<File>,
    ): String {

        return dependencyFiles
            .joinToString(delimiter) { file ->
                if (relativePathToFile.isNullOrEmpty()) {
                    file.path.replace('\\', '/')
                } else {
                    "$relativePathToFile/${file.name}"
                        .replace('\\', '/')
                }
            }
    }
}
