package de.roamingthings.wsepd

import de.roamingthings.ktgfx.*
import kwiringpi.*
import kwiringpi.PinMode.INPUT
import kwiringpi.PinMode.OUTPUT
import kwiringpi.SpiChannel.CHANNEL0
import kotlin.de.roamingthings.wsepd.*
import kotlin.de.roamingthings.wsepd.DisplayCommand.*

// Display resolution
private const val EPD_WIDTH = 176
private const val EPD_HEIGHT = 264

// Pin definition
private const val PIN_RST = 17
private const val PIN_DC = 25
private const val PIN_CS = 8
private const val PIN_BUSY = 24

// Pin level definition
const val LOW = 0
const val HIGH = 1

class Display : KtGfx(EPD_WIDTH, EPD_HEIGHT) {
    private var buffer: ByteArray = ByteArray(calculateBufferSizeInBytes(width, height)) { 0x00.toByte() }

    init {
        wiringPiSetupGpio()

        pinMode(PIN_RST, OUTPUT)
        pinMode(PIN_DC, OUTPUT)
        pinMode(PIN_BUSY, INPUT)

        wiringPiSPISetup(CHANNEL0, 2000000)

        initDisplay()
    }

    fun initDisplay() {
        reset()

        sendCommand(POWER_SETTING)
        sendData(0x03)                  // VDS_EN, VDG_EN
        sendData(0x00)                  // VCOM_HV, VGHL_LV[1], VGHL_LV[0]
        sendData(0x2b)                  // VDH
        sendData(0x2b)                  // VDL
        sendData(0x09)                  // VDHR

        sendCommand(BOOSTER_SOFT_START)
        sendData(0x07)
        sendData(0x07)
        sendData(0x17)
        // Power optimization
        sendCommand(0xF8)
        sendData(0x60)
        sendData(0xA5)
        // Power optimization
        sendCommand(0xF8)
        sendData(0x89)
        sendData(0xA5)
        // Power optimization
        sendCommand(0xF8)
        sendData(0x90)
        sendData(0x00)
        // Power optimization
        sendCommand(0xF8)
        sendData(0x93)
        sendData(0x2A)
        // Power optimization
        sendCommand(0xF8)
        sendData(0xA0)
        sendData(0xA5)
        // Power optimization
        sendCommand(0xF8)
        sendData(0xA1)
        sendData(0x00)
        // Power optimization
        sendCommand(0xF8)
        sendData(0x73)
        sendData(0x41)

        sendCommand(PARTIAL_DISPLAY_REFRESH)
        sendData(0x00)
        sendCommand(POWER_ON)

        waitUntilIdle()

        sendCommand(PANEL_SETTING)
        sendData(0xAF)        //KW-BF   KWR-AF    BWROTP 0f
        sendCommand(PLL_CONTROL)
        sendData(0x3A)       //3A 100HZ   29 150Hz 39 200HZ    31 171HZ
        sendCommand(VCM_DC_SETTING_REGISTER)
        sendData(0x12)
        delayMs(2)

        setLut()
    }

    fun refresh() {
        sendBufferToDisplay { buffer[it].inv() }
    }

    fun sendBufferToDisplay(byteBuffer: ByteArray) {
        sendBufferToDisplay { byteBuffer[it] }
    }

    fun sendBufferToDisplay(intBuffer: IntArray) {
        sendBufferToDisplay { intBuffer[it].toByte() }
    }

    fun copyBitmapToBuffer(bitmap: IntArray) {
        var bufferIndex = 0
        for (value in bitmap) {
            buffer[bufferIndex++] = value.toByte().inv()
        }
    }

    private fun sendBufferToDisplay(dataProvider: (Int) -> Byte) {
        sendCommand(DATA_START_TRANSMISSION_1)
        delayMs(2)
        for (pos in 0 until (EPD_WIDTH * EPD_HEIGHT / 8))
            sendData(0xFF)
        delayMs(2)

        sendCommand(DATA_START_TRANSMISSION_2)
        delayMs(2)
        for (pos in 0 until (EPD_WIDTH * EPD_HEIGHT / 8))
            sendData(dataProvider.invoke(pos))
        delayMs(2)

        sendCommand(DISPLAY_REFRESH)

        waitUntilIdle()
    }

    private fun reset() {
        digitalWrite(PIN_RST, LOW)                //module reset
        delayMs(200)
        digitalWrite(PIN_RST, HIGH)
        delayMs(200)
    }

    fun awake() {
        reset()
        initDisplay()
    }

    fun sleep() {
        sendCommand(POWER_OFF)
        delayMs(2)
        sendData(0xA5)
    }

    private fun sendCommand(command: Int) {
        digitalWrite(PIN_DC, LOW)
        spiTransfer(CHANNEL0, command.toByte())
    }

    private fun sendCommand(command: DisplayCommand) {
        sendCommand(command.value)
    }

    private fun sendData(data: Int) {
        sendData(data.toByte())
    }

    private fun sendData(data: Byte) {
        digitalWrite(PIN_DC, HIGH)
        spiTransfer(CHANNEL0, data)
    }

    private fun sendData(data: IntArray) {
        for (dataValue in data)
            sendData(dataValue.toByte())
    }

    private fun sendData(data: ByteArray) {
        digitalWrite(PIN_DC, HIGH)
        spiTransfer(CHANNEL0, data)
    }

    private fun waitUntilIdle() {
        while (digitalRead(PIN_BUSY) == 0) {      //0: busy, 1: idle
            delayMs(100)
        }
    }

    private fun setLut() {
        sendCommand(LUT_FOR_VCOM)                            //vcom
        sendData(lutVcomDc)

        sendCommand(LUT_WHITE_TO_WHITE)                      //ww --
        sendData(lutWw)

        sendCommand(LUT_BLACK_TO_WHITE)                      //bw r
        sendData(lutBw)

        // TODO lutBb and lutWb seem to be swapped in original driver/sample
        sendCommand(DisplayCommand.LUT_WHITE_TO_BLACK)                      //wb w
//        sendCommand(LUT_BLACK_TO_BLACK)                      //bb b
        sendData(lutBb)

        // TODO lutBb and lutWb seem to be swapped in original driver/sample
        sendCommand(DisplayCommand.LUT_BLACK_TO_BLACK)                      //bb b
//        sendCommand(LUT_WHITE_TO_BLACK)                      //wb w
        sendData(lutWb)
    }

    override fun drawPixel(x: Int, y: Int, color: Color) {
        if (isCoordinateOutsideCanvas(x, y)) return

        val transformed = transformCoordinateWithCurrentRotation(x, y)

        val bufferIndex = transformed.x / 8 + transformed.y * physicalWidth / 8

        buffer[bufferIndex] =
                if (color == Color.BLACK) (buffer[bufferIndex].toInt() or (1 shl (7 - transformed.x % 8))).toByte()
                else (buffer[bufferIndex].toInt() and (0x00FF xor (1 shl (7 - transformed.x * 8)))).toByte()
    }

    override fun fillScreen(color: Color) {
        val numBytes = calculateBufferSizeInBytes(physicalWidth, physicalHeight)
        buffer = ByteArray(numBytes) {
            (if (color == Color.BLACK) 0xFF else 0x00).toByte()
        }
    }

    private fun isCoordinateOutsideCanvas(x: Int, y: Int) = (x < 0) || (y < 0) || (x >= width) || (y >= height)

    private fun transformCoordinateWithCurrentRotation(x: Int, y: Int) =
            when (rotation) {
                Rotation.NONE -> Point(x, y)
                Rotation.DEGREE90 ->  Point(physicalWidth - 1 - y, x)
                Rotation.DEGREE180 -> Point(physicalWidth - 1 - x, physicalHeight - 1 - y)
                Rotation.DEGREE270 -> Point(y, physicalHeight - 1 - x)
            }

}

private fun calculateBufferSizeInBytes(width: Int, height: Int) = ((width + 7) / 8) * height
