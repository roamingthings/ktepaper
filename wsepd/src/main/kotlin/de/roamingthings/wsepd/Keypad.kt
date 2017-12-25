package de.roamingthings.wsepd

import de.roamingthings.wsepd.KeyPadEventType.*
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

        var currentKeypadState: Int = 0

        while (running) {
            val key1Value = key1.digitalState shl 0
            val key2Value = key2.digitalState shl 1
            val key3Value = key3.digitalState shl 2
            val key4Value = key4.digitalState shl 3

            val readKeypadState = (key1Value or key2Value or key3Value or key4Value).inv() and 0x0F

            if (currentKeypadState != readKeypadState) {

                val events = ArrayList<KeyPadEvent>()
                KeypadKey.values().forEach {
                    addEventForKeyIfEdgeDetected(it, currentKeypadState, readKeypadState, events)

                }

                currentKeypadState = readKeypadState
                yieldAll(events)
            } else {
                delayMs(100)
            }
        }
    }

}

private fun addEventForKeyIfEdgeDetected(key: KeypadKey, currentKeypadState: Int, readKeypadState: Int, events: ArrayList<KeyPadEvent>) {
    if (hasRisingEdge(currentKeypadState, readKeypadState, key.bitPosition)) events.add(KeyPadEvent(KEY_UP, key))
    else if (hasFallingEdge(currentKeypadState, readKeypadState, key.bitPosition)) events.add(KeyPadEvent(KEY_DOWN, key))
}

fun hasRisingEdge(oldStateField: Int, newStateBitfield: Int, bitNumber: Int): Boolean {
    val modifiedBits = oldStateField xor newStateBitfield
    val risenBits = (modifiedBits and oldStateField.inv())
    return (risenBits and (1 shl bitNumber) != 0)
}

fun hasFallingEdge(oldStateField: Int, newStateBitfield: Int, bitNumber: Int): Boolean {
    val modifiedBits = oldStateField xor newStateBitfield
    val fallenBits = (modifiedBits and oldStateField)
    return (fallenBits and (1 shl bitNumber) != 0)
}

class KeyPadEvent(val eventType: KeyPadEventType, val key: KeypadKey) {
    override fun toString(): String {
        return "KeyPadEvent(eventType=$eventType, keyNum=$key)"
    }
}

enum class KeypadKey(val keyCode: Int, val bitPosition: Int) {
    KEY1(1, 0),
    KEY2(2, 1),
    KEY3(3, 2),
    KEY4(4, 3);

    override fun toString(): String{
        return "KeypadKey(keyCode=$keyCode, bitPosition=$bitPosition)"
    }
}

enum class KeyPadEventType {
    KEY_DOWN, KEY_UP
}

fun Int.to8BitHexString(): String {
    val maskedValue = this and 0x00FF
    val result = StringBuilder("0x")
    if (maskedValue < 0x10) {
        result.append("0")
    }
    return result.append(maskedValue.toString(16)).toString()
}