import my.plugin.conventions.ApplicationConvention.isMyJar
import my.plugin.conventions.ReleaseInformationFactory
import proguard.gradle.ProGuardTask

/**
 * This plugin configures tasks for obfuscating applications and preparing
 * outputs for an installer.
 */

plugins {
    id("my.project.project-convention")
}

tasks {

    val releaseInformation = ReleaseInformationFactory(project)

    val obfuscatedJarsZipFile: Provider<String> =
        releaseInformation.releaseDir.map {
            relativePath(
                it.file("obfuscated.zip")
            )
        }

    @Suppress("unused")
    val obfuscatedJarsDir: Provider<String> =
        releaseInformation.releaseDir.map {
            relativePath(
                it.dir("obfuscated")
            )
        }

    /**
     * Obfuscates application code and copies dependency libraries to the
     * obfuscation output directory.
     */
    register<ProGuardTask>("obfuscate") {

        group = "release"
        description = "Obfuscates application code."

        // Task dependencies
        dependsOn(assemble)

        val proguardConfigFile: String = relativePath(
            rootProject.layout.projectDirectory.file(
                "shared-resources/obfuscation/proguard-configuration.pro"
            )
        )
        configuration(proguardConfigFile)

        val releaseDir = releaseInformation.releaseDir
        val printMappingOutputFile: Provider<String> =
            releaseDir.map { dir ->
                relativePath(dir.file("${project.name}-obfuscation.map"))
            }
        printmapping(printMappingOutputFile)

        val printSeedsOutputFile: Provider<String> =
            releaseDir.map { dir ->
                relativePath(dir.file("${project.name}.kept"))
            }
        printseeds(printSeedsOutputFile)

        // Obfuscate the project's JAR
        val projectJarPath: Provider<String> = jar.map { jarTask ->
            val jarName: String = jarTask.outputs.files.singleFile.name
            val jarFile: RegularFile =
                project.layout.buildDirectory.file("libs/$jarName").get()
            relativePath(jarFile)
        }

        injars(projectJarPath)

        // Obfuscate my project dependencies
        val inJarSet: Provider<Set<String>> =
            configurations.runtimeClasspath.map { config: Configuration ->

                val inJars: MutableSet<String> = mutableSetOf()

                config.forEach { file: File ->

                    if (isMyJar(file)) {
                        inJars.add(
                            relativePath(file)
                        )
                    }
                }

                inJars
            }

        injars(inJarSet)

        // Specify (but do not obfuscate) third party dependencies
        val libraryJarSet: Provider<Set<String>> =
            configurations.runtimeClasspath.map { config: Configuration ->

                val libraryJars: MutableSet<String> = mutableSetOf()

                config.forEach { file: File ->

                    if (!isMyJar(file)) {
                        // Use the absolute path because it will be relocatable
                        // when (and only when) users have the same Gradle user
                        // home. Relocatability using the relative path depends
                        // on both the root project location AND the Gradle user
                        // home locations and is less likely to occur.
                        libraryJars.add(file.absolutePath)
                    }
                }

                libraryJars
            }

        libraryjars(libraryJarSet)

        // FIXME: The ProGuard documentation states the outjars parameter
        //  accepts a single directory to which all obfuscated outputs will be
        //  directed; however, doing so causes the task to fail with an error
        //  indicating the specified directory is not a File. See
        //  https://www.guardsquare.com/manual/configuration/examples#restructuring
        outjars(obfuscatedJarsZipFile)

        // Write task configuration information to a file for debugging
        val debuggingOutputFile: Provider<RegularFile> =
            releaseInformation.debuggingDir.map { dir ->
                dir.file("${project.name}-$name.txt")
            }

        outputs.file(debuggingOutputFile)

        doFirst {

            val outputFile: File = debuggingOutputFile.get().asFile
            outputFile.parentFile.mkdirs()

            outputFile.writeText(
                "==============\nConfiguration:\n==============\n"
            )
            outputFile.appendText("configuration = $proguardConfigFile\n")
            outputFile.appendText("printmapping = ${printMappingOutputFile.get()}\n")
            outputFile.appendText("printseeds = ${printSeedsOutputFile.get()}\n")

            outputFile.appendText(
                "\n===========\ninJarFiles:\n===========\n"
            )
            inJarFiles.forEach {
                if (it is Provider<*>) {
                    outputFile.appendText("${it.get()}\n")
                } else {
                    outputFile.appendText("$it\n")
                }
            }

            outputFile.appendText(
                "\n=================\nlibraryJarFiles:\n=================\n"
            )
            libraryJarFiles.forEach {
                if (it is Provider<*>) {
                    outputFile.appendText("${it.get()}\n")
                } else {
                    outputFile.appendText("$it\n")
                }
            }

            outputFile.appendText(
                "\n============\noutJarFiles:\n============\n"
            )
            outJarFiles.forEach {
                if (it is Provider<*>) {
                    outputFile.appendText("${it.get()}\n")
                } else {
                    outputFile.appendText("$it\n")
                }
            }
        }
    }
}
