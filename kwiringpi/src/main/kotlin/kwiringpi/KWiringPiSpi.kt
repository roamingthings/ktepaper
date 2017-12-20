package kwiringpi

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.cValuesOf
import kotlinx.cinterop.toCValues
import libwiringpi.getErrno
import libwiringpi.getErrorMessage

fun wiringPiSPISetup(channel: SpiChannel, speed: Int): Int {
    val fd = libwiringpi.wiringPiSPISetup(channel.value, speed)
    if (fd < 0) {
        val errno = getErrno()
        val errorMessage = getErrorMessage()
        throw KWiringPiSpiException("Could not setup SPI. Resulting error $errorMessage ($errno)")
    }
    return fd
}

fun spiTransfer(channel: SpiChannel, data: Byte) {
//        println("Transfer SPI data " + data)

    var dataRef: CValuesRef<ByteVar> = cValuesOf(data)

    val result = libwiringpi.wiringPiSPIDataRW(channel.value, dataRef, 1)

    if (result < 0) {
        val errno = getErrno()
        val errorMessage = getErrorMessage()
        throw KWiringPiSpiException("WiringPi library returned $result for SPI transfer with errno $errorMessage ($errno)")
    }
}

fun spiTransfer(channel: SpiChannel, data: ByteArray) {
//        println("Transfer SPI data " + data)

    var dataRef: CValuesRef<ByteVar> = data.toCValues()

    val result = libwiringpi.wiringPiSPIDataRW(channel.value, dataRef, data.count())

    if (result < 0) {
        val errno = getErrno()
        val errorMessage = getErrorMessage()
        throw KWiringPiSpiException("WiringPi library returned $result for SPI transfer with errno $errorMessage ($errno)")
    }
}

enum class SpiChannel(val value: Int) {
    CHANNEL0(0),
    CHANNEL1(1)
}
