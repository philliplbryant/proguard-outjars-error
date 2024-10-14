# proguard-outjars-error
Uses the ProGuard Gradle plugin (version 7.5.0) to output all obfuscated jars (outjars) to a single directory for the purpose of demonstrating the plugin does not appear to support this feature as [documented](https://www.guardsquare.com/manual/configuration/examples#restructuring):

> If you want to preserve the structure of your input jars (and/or apks, aars, wars, ears, jmods, zips, or directories), you can specify an output directory (or an apk, an aar, a war, an ear, a jmod, or a zip). For example:
>
>-injars  in1.jar
-injars  in2.jar
-injars  in3.jar
-outjars out
>
>The input jars will then be reconstructed in the directory out, with their original names.

Also note that specifying the single output directory in the ProGuard [configuration file](./shared-resources/obfuscation/proguard-configuration.pro) appears to work as documented above and demonstrated below.

## Testing
1. Execute `gradlew obfuscate` from the root project directory.
1. Observe the [single Zip file](./my-application/build/release/obfuscated.zip), `build/release/obfuscated.zip`, containing the obfuscated JARs is output.
1. Modify the [ProGuard Gradle plugin configuration](./build-logic/src/main/kotlin/my.release.proguard-convention.gradle.kts), `build-logic/src/main/kotlin/my.release.proguard-convention.gradle.kts`, to output to a directory instead of a Zip file by changing `outjars(obfuscatedJarsZipFile)` to `outjars(obfuscatedJarsDir)` as marked with the `FIXME` comment.
1. Execute `gradlew obfuscate` from the root project directory.
1. Observe an exception is thrown, indicating something similar to:
>Expected 'C:/Demos/proguard-outjars-error/my-application/build/release/obfuscated' to be a file
6. Comment out the aforementioned `outjars` line altogether
1. Add `-outjars my-application/build/release/obfuscated` to the [ProGuard configuration file](./shared-resources/obfuscation/proguard-configuration.pro), `shared-resources/obfuscation/proguard-configuration.pro`, below `-basedirectory ../../`
1. Execute `gradlew obfuscate` from the root project directory.
1. Observe the [single directory](./my-application/build/release/obfuscated), `my-application/build/release/obfuscated`, containing the obfuscated JARs is output.
