package de.roamingthings.wsepd

import kotlin.test.Test
import kotlin.test.assertTrue
import de.roamingthings.wsepd.hasRisingEdge

class KeypadTest {
    @Test
    fun `should detect rising edge`() {
        val oldState = 0x00
        val newState = 0x01

        val edgeDetected = hasRisingEdge(oldState, newState, 0)

        assertTrue(edgeDetected)
    }
}