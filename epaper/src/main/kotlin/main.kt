import de.roamingthings.ktgfx.Color.Companion.BLACK
import de.roamingthings.ktgfx.Rotation.DEGREE270
import de.roamingthings.ktgfx.font.FreeSans12pt7b
import de.roamingthings.wsepd.*

fun main(args: Array<String>) {
    showSampleScreen()

    inputLoop()
}

private fun inputLoop() {
    val keyPad = KeyPad()
    println("Waiting for input. Press KEY4 to exit")
    var exit = false
    val keypadInputIterator = keyPad.keypadInputSequence().iterator()
    while (keypadInputIterator.hasNext() && !exit) {
        val keyPadEvent = keypadInputIterator.next()
        println("Key-Event: $keyPadEvent")
        exit = (keyPadEvent.key == KeypadKey.KEY4 && keyPadEvent.eventType == KeyPadEventType.KEY_UP)
    }
}

private fun showSampleScreen() {
    println("Creating screen content")
    val display = Display()

    display.drawBitmapInverted(0, 0, welcomeImageBuffer, 176, 264, BLACK)

    display.drawLine(0, 0, 10, 10, BLACK)

    display.drawRect(0, 15, 10, 10, BLACK)
    display.fillRect(0, 30, 10, 10, BLACK)

    display.drawCircle(5, 50, 5, BLACK)
    display.fillCircle(5, 65, 5, BLACK)

    display.drawRoundRect(20, 0, 10, 10, 2, BLACK)
    display.fillRoundRect(20, 15, 10, 10, 2, BLACK)

    display.drawTriangle(20, 30, 30, 30, 25, 40, BLACK)
    display.fillTriangle(20, 55, 30, 55, 25, 45, BLACK)

    display.gfxFont = FreeSans12pt7b
    display.textColor = BLACK
    display.rotation = DEGREE270

    display.setCursor(5, 152)
    display.write("Dynamic text generation")

    println("Refreshing display")
    display.refresh()
    println("Display job done")
}

