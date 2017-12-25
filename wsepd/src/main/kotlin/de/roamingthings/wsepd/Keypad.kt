package de.roamingthings.wsepd

import kwiringpi.PiZeroWGpio
import kwiringpi.PullUpDownMode
import kwiringpi.KWiringPi.delayMs
import kotlin.coroutines.experimental.buildSequence


class KeyPad {

    var running: Boolean = false

    fun keypadInputSequence() = buildSequence {
        running = true

        val key1 = PiZeroWGpio.GPIO21.gpio.pullUpDnControl(PullUpDownMode.PULL_UP)
        val key2 = PiZeroWGpio.GPIO22.gpio.pullUpDnControl(PullUpDownMode.PULL_UP)
        val key3 = PiZeroWGpio.GPIO23.gpio.pullUpDnControl(PullUpDownMode.PULL_UP)
        val key4 = PiZeroWGpio.GPIO24.gpio.pullUpDnControl(PullUpDownMode.PULL_UP)

        var currentKeypadMask: Int = 0

        while (running) {
            val key1Value = key1.digitalState shl 0
            val key2Value = key2.digitalState shl 1
            val key3Value = key3.digitalState shl 2
            val key4Value = key4.digitalState shl 3

            val keyMask = (key1Value or key2Value or key3Value or key4Value).inv() and 0x0F

            if (currentKeypadMask != keyMask) {
                val keyModifyMask = keyMask xor currentKeypadMask
                val keyDownMask = (keyModifyMask and keyMask) and 0x0f
                val keyUpMask = (keyModifyMask and keyMask.inv()) and 0x0f

                println("Key down: 0x${keyDownMask.toString(16)} - Key up: 0x${keyUpMask.toString(16)}")
                println("Key down: 0b${keyDownMask.toString(2)} - Key up: 0b${keyUpMask.toString(2)}")
                currentKeypadMask = keyMask
                yield(keyMask)
            } else {
                delayMs(100)
            }
        }
    }
}

class KeyPadEvent(val eventType: KeyPadEventType, val keyNum: Int)

enum class KeyPadEventType {
    KEY_DOWN, KEY_UP
}