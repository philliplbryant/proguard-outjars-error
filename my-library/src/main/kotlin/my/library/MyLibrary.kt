package my.library

import org.apache.logging.log4j.kotlin.Logging

class MyLibrary {
    fun getGreeting() = "Hello from ${MyLibrary::class.qualifiedName}!"
}

class ThirdPartyLibrary {
    fun getGreeting() = "Hello from ${Logging::class.qualifiedName}!"
}
