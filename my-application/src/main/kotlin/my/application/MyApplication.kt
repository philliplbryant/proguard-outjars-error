package my.application

import my.library.MyLibrary
import my.library.ThirdPartyLibrary
import javax.swing.JOptionPane
import kotlin.system.exitProcess

fun main() {

    val myApplicationGreeting = MyApplication().getGreeting()
    val myLibraryGreeting = MyLibrary().getGreeting()
    val thirdPartyLibraryGreeting = ThirdPartyLibrary().getGreeting()

    val message = "${myApplicationGreeting}\n" +
            "${myLibraryGreeting}\n" +
            thirdPartyLibraryGreeting

    JOptionPane.showMessageDialog(
        null,
        message,
    )

    exitProcess(0)
}

class MyApplication {
    fun getGreeting() = "Hello from ${MyApplication::class.qualifiedName}!"
}
