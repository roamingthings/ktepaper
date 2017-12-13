package kwiringpi

fun wiringPiSetupGpio() =
        libwiringpi.wiringPiSetupGpio()

fun pinMode(pin: Int, mode: PinMode) =
        libwiringpi.pinMode(pin, mode.value)

fun digitalWrite(pin: Int, value: Int) =
        libwiringpi.digitalWrite(pin, value)

fun digitalRead(pin: Int): Int =
        libwiringpi.digitalRead(pin)

fun delayMs(delayTime: Int) =
        libwiringpi.delay(delayTime)

enum class PinMode(val value: Int) {
    INPUT(libwiringpi.INPUT),
    OUTPUT(libwiringpi.OUTPUT)
}
