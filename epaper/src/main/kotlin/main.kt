import de.roamingthings.wsepd.*
import de.roamingthings.wsepd.welcomeImageBuffer

fun main(args: Array<String>) {
    val display = Display()

    println("Sending image to display")
    display.displayFrame(welcomeImageBuffer)
    println("Finished")
}

