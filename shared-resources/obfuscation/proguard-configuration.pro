
# ProGuard configuration file used for all applications.

#-------------------------------------------------------------------------------
# General settings
#-------------------------------------------------------------------------------

# Paths defined in this file are relative to the `basedirectory` path, which is
# defined here to be the root project directory.  By default, paths are relative
# to the directory containing this file.
-basedirectory ../../

# Enable verbose obfuscation output for debugging
-verbose

# Print warnings about unresolved references, but continue processing
#-ignorewarnings

# FIXME Remove target when updating JDK version > 11.  target is deprecated as
#  the option is only applicable for Java class file versions <= 11 (see also
#  https://www.guardsquare.com/manual/configuration/usage#target)
# The JDK version
-target 11

# Obfuscate class member names and also replace usages of class name strings
# (e.g. when using reflection/introspection).
-useuniqueclassmembernames
-adaptclassstrings

# Keep line number information
-renamesourcefileattribute SourceFile

#-------------------------------------------------------------------------------
# Common Libraries to be included
#-------------------------------------------------------------------------------

# Java Runtime Jars
-libraryjars <java.home>/lib/(*.jar;)
-libraryjars <java.home>/jmods/java.base.jmod(!**.jar;!module-info.class)
-libraryjars <java.home>/jmods/java.desktop.jmod(!**.jar;!module-info.class)

#-------------------------------------------------------------------------------
# Obfuscation exclusions ("keep" settings)
#-------------------------------------------------------------------------------

# Preserve all application entry points.
-keepclasseswithmembers public class * {
    public static void main(java.lang.String[]);
}

-renamesourcefileattribute SourceFile
-keepattributes Signature,Exceptions,*Annotation*,
                InnerClasses,PermittedSubclasses,EnclosingMethod,
                Deprecated,SourceFile,LineNumberTable

-keepclassmembers,allowoptimization enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
