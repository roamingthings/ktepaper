import de.roamingthings.ktgfx.Color
import de.roamingthings.ktgfx.Color.Companion.BLACK
import de.roamingthings.ktgfx.Rotation.DEGREE270
import de.roamingthings.ktgfx.font.FreeSans12pt7b
import de.roamingthings.wsepd.*
import de.roamingthings.wsepd.KeyPadEventType.*
import de.roamingthings.wsepd.KeypadKey.*


private const val EPD_WIDTH = 176
private const val EPD_HEIGHT = 264

private const val ACTIVE_INDICATOR_INSET = 5
private const val ACTIVE_INDICATOR_WIDTH = 10
private const val ACTIVE_INDICATOR_HEIGHT = EPD_WIDTH / 4 - 2 * ACTIVE_INDICATOR_INSET


fun main(args: Array<String>) {
    // a global val leads to Segfault
    val display = Display()

    showSampleScreen(display)

    inputLoop(display)
}

private fun inputLoop(display: Display) {
    val keyPad = KeyPad()
    println("Waiting for input. Press KEY4 to exit")
    var exit = false
    val keypadInputIterator = keyPad.keypadInputSequence().iterator()
    while (keypadInputIterator.hasNext() && !exit) {
        val keyPadEvent = keypadInputIterator.next()
        println("Key-Event: $keyPadEvent")

        if (keyPadEvent.eventType == KEY_UP) {
            display.fillScreen(Color.WHITE)
            display.rotation = DEGREE270

            display.gfxFont = FreeSans12pt7b
            display.textColor = BLACK
            display.rotation = DEGREE270

            display.setCursor(ACTIVE_INDICATOR_WIDTH * 2, 24 + calculateTopOfRow(1))
            display.write("Task 1 (15:23)")
            display.setCursor(ACTIVE_INDICATOR_WIDTH * 2, 24 + calculateTopOfRow(2))
            display.write("Task 2 (12:15)")
            display.setCursor(ACTIVE_INDICATOR_WIDTH * 2, 24 + calculateTopOfRow(3))
            display.write("Task 3 (10:09)")
            display.setCursor(ACTIVE_INDICATOR_WIDTH * 2, 24 + calculateTopOfRow(4))
            display.write("Task 4 (03:17)")


            when (keyPadEvent.key) {
                KEY1 -> {
                    drawActiveIndicator(display, 1)
                    drawInactiveIndicator(display, 2)
                    drawInactiveIndicator(display, 3)
                    drawInactiveIndicator(display, 4)
                }
                KEY2 -> {
                    drawInactiveIndicator(display, 1)
                    drawActiveIndicator(display, 2)
                    drawInactiveIndicator(display, 3)
                    drawInactiveIndicator(display, 4)
                }
                KEY3 -> {
                    drawInactiveIndicator(display, 1)
                    drawInactiveIndicator(display, 2)
                    drawActiveIndicator(display, 3)
                    drawInactiveIndicator(display, 4)
                }
                KEY4 -> {
                    drawInactiveIndicator(display, 1)
                    drawInactiveIndicator(display, 2)
                    drawInactiveIndicator(display, 3)
                    drawActiveIndicator(display, 4)
                }
            }

            display.refresh()
        }

        exit = (keyPadEvent.key == KEY4 && keyPadEvent.eventType == KEY_UP)
    }
}

private fun drawInactiveIndicator(display: Display, row: Int) {
    display.drawRect(0, calculateTopOfRow(row), ACTIVE_INDICATOR_WIDTH, ACTIVE_INDICATOR_HEIGHT, BLACK)
}

private fun drawActiveIndicator(display: Display, row: Int) {
    display.fillRect(0, calculateTopOfRow(row), ACTIVE_INDICATOR_WIDTH, ACTIVE_INDICATOR_HEIGHT, BLACK)
}

private fun calculateTopOfRow(rowNum: Int): Int =
        (rowNum - 1) * ACTIVE_INDICATOR_HEIGHT + (rowNum - 1) * 2 * ACTIVE_INDICATOR_INSET + ACTIVE_INDICATOR_INSET

private fun showSampleScreen(display: Display) {
    println("Creating screen content")

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
    display.write("Press a key to continue")

    println("Refreshing display")
    display.refresh()
    println("Display job done")
}

