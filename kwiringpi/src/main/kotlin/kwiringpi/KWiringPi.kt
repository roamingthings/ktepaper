package kwiringpi

object KWiringPi {
    init {
        println("wiringPiSetupGpio")
        libwiringpi.wiringPiSetupGpio()
    }

    fun pinMode(pin: Int, mode: Int) =
            libwiringpi.pinMode(pin, mode)

    fun pullUpDnControl(pin: Int, pud: Int) =
            libwiringpi.pullUpDnControl(pin, pud)

    fun digitalWrite(pin: Int, pinState: Int) =
            libwiringpi.digitalWrite(pin, pinState)

    fun digitalRead(pin: Int): Int =
            libwiringpi.digitalRead(pin)

    fun delayMs(delayTime: Int) =
            libwiringpi.delay(delayTime)
}

class Gpio(val pinNumber: Int, initialPinMode: GpioPinMode = GpioPinMode.INPUT, initialPullUpDownMode: PullUpDownMode = PullUpDownMode.OFF) {

    init {
        pinMode(initialPinMode)
        pullUpDnControl(initialPullUpDownMode)
    }

    var digitalState: DigitalPinState
        get() =
            digitalRead()
        set(newDigitalState) {
            digitalWrite(newDigitalState)
        }

    fun pinMode(mode: GpioPinMode): Gpio {
        KWiringPi.pinMode(pinNumber, mode.value)
        return this
    }

    fun pullUpDnControl(pullUpDownMode: PullUpDownMode): Gpio {
        KWiringPi.pullUpDnControl(pinNumber, pullUpDownMode.value)
        return this
    }

    private fun digitalRead(): DigitalPinState =
            DigitalPinState.valueOf(KWiringPi.digitalRead(pinNumber))

    private fun digitalWrite(pinState: DigitalPinState) =
            KWiringPi.digitalWrite(pinNumber, pinState.value)
}


enum class GpioPinMode(val value: Int) {
    INPUT(libwiringpi.INPUT),
    OUTPUT(libwiringpi.OUTPUT)
}

enum class PullUpDownMode(val value: Int) {
    OFF(libwiringpi.PUD_OFF),
    PULL_DOWN(libwiringpi.PUD_DOWN),
    PULL_UP(libwiringpi.PUD_UP)
}

enum class DigitalPinState(val value: Int) {
    LOW(0),
    HIGH(1);

    infix fun shl(bitCount: Int): Int = value shl bitCount
    infix fun shr(bitCount: Int): Int = value shl bitCount

    companion object {
        fun valueOf(stateValue: Int): DigitalPinState =
                when (stateValue) {
                    0 -> DigitalPinState.LOW
                    1 -> DigitalPinState.HIGH
                    else -> throw IllegalArgumentException("State can either be 0 or 1")
                }
    }
}

enum class PiZeroWGpio(val pinName: String, val bcmPinNumber: Int, val wPiPinNumber: Int) {
    SDA1("SDA.1", 2, 8),
    SCL1("SCL.1", 3, 9),
    GPIO7("GPIO.7", 4, 7),
    GPIO0("GPIO.0", 17, 0),
    GPIO2("GPIO.2", 27, 2),
    GPIO3("GPIO.3", 22, 3),
    MOSI("MOSI", 10, 12),
    MISO("MISO", 9, 13),
    SCLK("SCLK", 11, 14),
    SDA0("SDA.0", 0, 30),
    GPIO21("GPIO.21", 5, 21),
    GPIO22("GPIO.22", 6, 22),
    GPIO23("GPIO.23", 13, 23),
    GPIO24("GPIO.24", 19, 24),
    GPIO25("GPIO.25", 26, 25),
    TXD("TxD", 14, 15),
    RXD("RxD", 15, 16),
    GPIO1("GPIO.1", 18, 1),
    GPIO4("GPIO.4", 23, 4),
    GPIO5("GPIO.5", 24, 5),
    GPIO6("GPIO.6", 25, 6),
    CE0("CE0", 8, 10),
    CE1("CE1", 7, 11),
    SCL0("SCL.0", 1, 31),
    GPIO26("GPIO.26", 12, 26),
    GPIO27("GPIO.27", 16, 27),
    GPIO28("GPIO.28", 20, 28),
    GPIO29("GPIO.29", 21, 29);

    val gpio: Gpio by lazy {
        Gpio(bcmPinNumber)
    }
}
