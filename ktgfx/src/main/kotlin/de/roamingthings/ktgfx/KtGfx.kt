package de.roamingthings.ktgfx

/*
 * This implementation is inspired by the Adafruit-Gfx-Library (https://github.com/adafruit/Adafruit-GFX-Library)
 */

import de.roamingthings.ktgfx.KtGfx.Corner.*
import de.roamingthings.ktgfx.font.classicStandardFont
import platform.posix.abs

abstract class KtGfx(protected val physicalWidth: Int, protected val physicalHeight: Int) {
    protected var width = physicalWidth
    protected var height = physicalHeight
    var cursorX = 0
    var cursorY = 0
    var textSize = 1
        set(value) {
            field = if (value > 0) value else 1
        }
    var textColor = Color.BLACK
        set(value) {
            field = value
            textBg = value
        }
    var textBg = Color.WHITE
    var wrap = true
    var gfxFont: GfxFont? = null
        set(value) {
            if (value != null) {
                if (gfxFont == null) {
                    // Switching from classic to new font behavior.
                    // Move cursor pos down 6 pixels so it's on baseline.
                    cursorY += 6
                }
            } else if (gfxFont != null) {
                // Switching from new to classic font behavior.
                // Move cursor pos up 6 pixels so it's at top-left of char.
                cursorY -= 6
            }
            field = value
        }
    var rotation = Rotation.NONE
        set(value) {
            field = value
            when (value) {
                Rotation.NONE, Rotation.DEGREE180 -> {
                    width = physicalWidth
                    height = physicalHeight
                }
                Rotation.DEGREE90, Rotation.DEGREE270 -> {
                    width = physicalHeight
                    height = physicalWidth
                }
            }
        }

    fun setCursor(x: Int, y: Int) {
        cursorX = x
        cursorY = y
    }

    abstract fun drawPixel(x: Int, y: Int, color: Color)

    fun writeLine(x0In: Int, y0In: Int, x1In: Int, y1In: Int, color: Color) {
        val steep = abs(y1In - y0In) > abs(x1In - x0In)

        var x0 = x0In
        var y0 = y0In
        var x1 = x1In
        var y1 = y1In

        if (steep) {
            val t = x0
            x0 = y0
            y0 = t

            val t2 = x1
            x1 = y1
            y1 = t2
        }

        if (x0 > x1) {
            val t = x0
            x0 = x1
            x1 = t

            val t2 = y0
            y0 = y1
            y1 = t2
        }

        val dx = x1 - x0
        val dy = abs(y1 - y0)

        var err: Int = dx / 2
        val ystep = if (y0 < y1) 1 else -1

        while (x0 <= x1) {
            if (steep) {
                writePixel(y0, x0, color)
            } else {
                writePixel(x0, y0, color)
            }

            err -= dy
            if (err < 0) {
                y0 += ystep
                err += dx
            }
            x0++
        }
    }

    open fun startWrite() {
    }

    open fun writePixel(x: Int, y: Int, color: Color) {
        drawPixel(x, y, color)
    }

    /**
     * (x,y) is topmost point; if unsure, calling function
     * should sort endpoints or call writeLine() instead
     */
    open fun writeFastVLine(x: Int, y: Int, h: Int, color: Color) {
        // Overwrite in subclasses if startWrite is defined!
        // Can be just writeLine(x, y, x, y+h-1, color);
        // or writeFillRect(x, y, 1, h, color);
        drawFastVLine(x, y, h, color)
    }

    /**
     * (x,y) is leftmost point; if unsure, calling function
     * should sort endpoints or call writeLine() instead
     */
    open fun writeFastHLine(x: Int, y: Int, w: Int, color: Color) {
        // Overwrite in subclasses if startWrite is defined!
        // Example: writeLine(x, y, x+w-1, y, color);
        // or writeFillRect(x, y, w, 1, color);
        drawFastHLine(x, y, w, color)
    }

    open fun writeFillRect(x: Int, y: Int, w: Int, h: Int, color: Color) {
        fillRect(x, y, w, h, color);
    }

    open fun endWrite() {
    }

    /**
     * (x,y) is topmost point; if unsure, calling function
     * should sort endpoints or call drawLine() instead
     */
    open fun drawFastVLine(x: Int, y: Int, h: Int, color: Color) {
        startWrite()
        writeLine(x, y, x, y + h - 1, color)
        endWrite()
    }

    /**
     * (x,y) is leftmost point; if unsure, calling function
     * should sort endpoints or call drawLine() instead
     */
    open fun drawFastHLine(x: Int, y: Int, w: Int, color: Color) {
        startWrite()
        writeLine(x, y, x + w - 1, y, color)
        endWrite()
    }

    fun fillRect(x: Int, y: Int, w: Int, h: Int, color: Color) {
        startWrite()
        for (i in x until x + w) {
            writeFastVLine(i, y, h, color)
        }
        endWrite()
    }

    open fun fillScreen(color: Color) {
        fillRect(0, 0, width, height, color)
    }

    open fun drawLine(x0In: Int, y0In: Int, x1In: Int, y1In: Int, color: Color) {
        var x0 = x0In
        var x1 = x1In
        var y0 = y0In
        var y1 = y1In

        when {
            x0 == x1 -> {
                if (y0 > y1) {
                    val t = y0
                    y0 = y1
                    y1 = t
                }
                drawFastVLine(x0, y0, y1 - y0 + 1, color)
            }
            y0 == y1 -> {
                if (x0 > x1) {
                    val t = x0
                    x0 = x1
                    x1 = t
                }
                drawFastHLine(x0, y0, x1 - x0 + 1, color)
            }
            else -> {
                startWrite()
                writeLine(x0, y0, x1, y1, color)
                endWrite()
            }
        }
    }

    fun drawCircle(x0: Int, y0: Int, r: Int, color: Color) {
        var f = 1 - r
        var ddF_x = 1
        var ddF_y = -2 * r
        var x = 0
        var y = r

        startWrite()
        writePixel(x0, y0 + r, color)
        writePixel(x0, y0 - r, color)
        writePixel(x0 + r, y0, color)
        writePixel(x0 - r, y0, color)

        while (x < y) {
            if (f >= 0) {
                y--
                ddF_y += 2
                f += ddF_y
            }
            x++
            ddF_x += 2
            f += ddF_x

            writePixel(x0 + x, y0 + y, color)
            writePixel(x0 - x, y0 + y, color)
            writePixel(x0 + x, y0 - y, color)
            writePixel(x0 - x, y0 - y, color)
            writePixel(x0 + y, y0 + x, color)
            writePixel(x0 - y, y0 + x, color)
            writePixel(x0 + y, y0 - x, color)
            writePixel(x0 - y, y0 - x, color)
        }
        endWrite()
    }

    protected open fun drawCircleHelper(x0: Int, y0: Int, r: Int, corner: Corner, color: Color) {
        var f = 1 - r
        var ddF_x = 1
        var ddF_y = -2 * r
        var x = 0
        var y = r

        while (x < y) {
            if (f >= 0) {
                y--
                ddF_y += 2
                f += ddF_y
            }
            x++
            ddF_x += 2
            f += ddF_x
            when (corner) {
                BOTTOM_RIGHT -> {
                    writePixel(x0 + x, y0 + y, color)
                    writePixel(x0 + y, y0 + x, color)
                }
                TOP_RIGHT -> {
                    writePixel(x0 + x, y0 - y, color)
                    writePixel(x0 + y, y0 - x, color)
                }
                BOTTOM_LEFT -> {
                    writePixel(x0 - y, y0 + x, color)
                    writePixel(x0 - x, y0 + y, color)
                }
                TOP_LEFT -> {
                    writePixel(x0 - y, y0 - x, color)
                    writePixel(x0 - x, y0 - y, color)
                }
            }
        }
    }

    fun fillCircle(x0: Int, y0: Int, r: Int, color: Color) {
        startWrite()
        writeFastVLine(x0, y0 - r, 2 * r + 1, color)
        fillCircleHelper(x0, y0, r, 3, 0, color)
        endWrite()
    }

    protected open fun fillCircleHelper(x0: Int, y0: Int, r: Int, cornername: Int, delta: Int, color: Color) {
        var f = 1 - r
        var ddF_x = 1
        var ddF_y = -2 * r
        var x = 0
        var y = r

        while (x < y) {
            if (f >= 0) {
                y--
                ddF_y += 2
                f += ddF_y
            }
            x++
            ddF_x += 2
            f += ddF_x

            if (cornername and 0x01 == 0x01) {
                writeFastVLine(x0 + x, y0 - y, 2 * y + 1 + delta, color)
                writeFastVLine(x0 + y, y0 - x, 2 * x + 1 + delta, color)
            }
            if (cornername and 0x02 == 0x02) {
                writeFastVLine(x0 - x, y0 - y, 2 * y + 1 + delta, color)
                writeFastVLine(x0 - y, y0 - x, 2 * x + 1 + delta, color)
            }
        }
    }

    fun drawRect(x: Int, y: Int, w: Int, h: Int, color: Color) {
        startWrite()
        writeFastHLine(x, y, w, color)
        writeFastHLine(x, y + h - 1, w, color)
        writeFastVLine(x, y, h, color)
        writeFastVLine(x + w - 1, y, h, color)
        endWrite()
    }

    fun drawRoundRect(x: Int, y: Int, w: Int, h: Int, r: Int, color: Color) {
        startWrite()
        // Top
        writeFastHLine(x + r, y, w - 2 * r, color)
        // Bottom
        writeFastHLine(x + r, y + h - 1, w - 2 * r, color)
        // Left
        writeFastVLine(x, y + r, h - 2 * r, color)
        // Right
        writeFastVLine(x + w - 1, y + r, h - 2 * r, color)

        // draw four corners
        drawCircleHelper(x + r, y + r, r, TOP_LEFT, color)
        drawCircleHelper(x + w - r - 1, y + r, r, TOP_RIGHT, color)
        drawCircleHelper(x + w - r - 1, y + h - r - 1, r, BOTTOM_RIGHT, color)
        drawCircleHelper(x + r, y + h - r - 1, r, BOTTOM_LEFT, color)
        endWrite()
    }

    fun fillRoundRect(x: Int, y: Int, w: Int, h: Int, r: Int, color: Color) {
        startWrite()
        writeFillRect(x + r, y, w - 2 * r, h, color)

        // draw four corners
        fillCircleHelper(x + w - r - 1, y + r, r, 1, h - 2 * r - 1, color)
        fillCircleHelper(x + r, y + r, r, 2, h - 2 * r - 1, color)
        endWrite()
    }

    fun drawTriangle(x0: Int, y0: Int, x1: Int, y1: Int, x2: Int, y2: Int, color: Color) {
        drawLine(x0, y0, x1, y1, color)
        drawLine(x1, y1, x2, y2, color)
        drawLine(x2, y2, x0, y0, color)
    }

    fun fillTriangle(x0In: Int, y0In: Int, x1In: Int, y1In: Int, x2In: Int, y2In: Int, color: Color) {
        var a: Int
        var b: Int
        var y: Int
        val last: Int
        var x0 = x0In
        var y0 = y0In
        var x1 = x1In
        var y1 = y1In
        var x2 = x2In
        var y2 = y2In

        // Sort coordinates by Y order (y2 >= y1 >= y0)
        if (y0 > y1) {
            val t = y0
            y0 = y1
            y1 = t

            val t2 = x0
            x0 = x1
            x1 = t2
        }
        if (y1 > y2) {
            val t = y1
            y1 = y2
            y2 = t

            val t2 = x1
            x1 = x2
            x2 = t2

        }
        if (y0 > y1) {
            val t = y0
            y0 = y1
            y1 = t

            val t2 = x0
            x0 = x1
            x1 = t2
        }

        startWrite();
        // All-on-same-line case
        if (y0 == y2) {
            a = x0
            b = x0

            if (x1 < a) a = x1
            else if (x1 > b) b = x1

            if (x2 < a) a = x2
            else if (x2 > b) b = x2

            writeFastHLine(a, y0, b - a + 1, color)
            endWrite()

            return
        }

        val dx01 = x1 - x0
        val dy01 = y1 - y0
        val dx02 = x2 - x0
        val dy02 = y2 - y0
        val dx12 = x2 - x1
        val dy12 = y2 - y1

        var sa: Int = 0
        var sb: Int = 0

        // For upper part of triangle, find scanline crossings for segments
        // 0-1 and 0-2.  If y1=y2 (flat-bottomed triangle), the scanline y1
        // is included here (and second loop will be skipped, avoiding a /0
        // error there), otherwise scanline y1 is skipped here and handled
        // in the second loop...which also avoids a /0 error here if y0=y1
        // (flat-topped triangle).
        last = if (y1 == y2) y1    // Include y1 scanline
        else y1 - 1 // Skip it

        y = y0
        while (y <= last) {
            a = x0 + sa / dy01
            b = x0 + sb / dy02
            sa += dx01
            sb += dx02
            /* longhand:
            a = x0 + (x1 - x0) * (y - y0) / (y1 - y0)
            b = x0 + (x2 - x0) * (y - y0) / (y2 - y0)
            */
            if (a > b) {
                val t = a
                a = b
                b = t
            }
            writeFastHLine(a, y, b - a + 1, color)
            y++
        }

        // For lower part of triangle, find scanline crossings for segments
        // 0-2 and 1-2.  This loop is skipped if y1=y2.
        sa = dx12 * (y - y1)
        sb = dx02 * (y - y0)
        while (y <= 2) {
            a = x1 + sa / dy12
            b = x0 + sb / dy02
            sa += dx12
            sb += dx02
            /* longhand:
            a = x1 + (x2 - x1) * (y - y1) / (y2 - y1)
            b = x0 + (x2 - x0) * (y - y0) / (y2 - y0)
            */
            if (a > b) {
                val t = a
                a = b
                b = t
            }
            writeFastHLine(a, y, b - a + 1, color)

            y++
        }
        endWrite()
    }

    fun drawChar(x: Int, y: Int, c: Char, color: Color, bg: Color, size: Int) {
        val cValue = c.toInt()
        if (gfxFont == null) drawClassicChar(x, y, cValue, color, bg, size)
        else  drawCustomFontChar(x, y, cValue, color, size)
    }

    /**
     * Character is assumed previously filtered to eliminate
     * newlines, returns, non-printable characters, etc.
     */
    private fun drawCustomFontChar(x: Int, y: Int, charValue: Int, foreground: Color, size: Int) {
        val fontCharIndex = charValue - gfxFont!!.first
        val bitmapArray = gfxFont!!.bitmap

        val glyph = gfxFont!!.glyph[fontCharIndex]

        var bo = glyph.bitmapOffset
        val w = glyph.width
        val h = glyph.height
        val xo = glyph.xOffset
        val yo = glyph.yOffset
        var bits = 0
        var bit = 0
        var xo16 = 0
        var yo16 = 0

        if (size > 1) {
            xo16 = xo
            yo16 = yo
        }

        startWrite();
        for (yy in 0 until h) {
            for (xx in 0 until w) {
                if ((bit++ and 7) == 0) {
                    bits = bitmapArray[bo++]
                }
                if (bits and 0x80 != 0) {
                    if (size == 1) {
                        writePixel(x + xo + xx, y + yo + yy, foreground)
                    } else {
                        writeFillRect(x + (xo16 + xx) * size, y + (yo16 + yy) * size,
                                size, size, foreground)
                    }
                }
                bits = bits shl 1
            }
        }
        endWrite()

    }
    private fun drawClassicChar(x: Int, y: Int, charValue: Int, foreground: Color, background: Color, size: Int) {
        if ((x >= width) || // Clip right
                (y >= height) || // Clip bottom
                ((x + 6 * size - 1) < 0) || // Clip left
                ((y + 8 * size - 1) < 0))   // Clip top
            return

        val correctedCharValue = if ((charValue >= 176)) charValue+1 else charValue

        startWrite()
        for (i in 0 until 5) {
            var line = classicStandardFont[correctedCharValue * 5 + i]
            for (j in 0 until 8) {
                if (line and 1 == 1) {
                    if (size == 1)
                        writePixel(x + i, y + j, foreground)
                    else
                        writeFillRect(x + i * size, y + j * size, size, size, foreground)
                } else if (background != foreground) {
                    if (size == 1)
                        writePixel(x + i, y + j, background)
                    else
                        writeFillRect(x + i * size, y + j * size, size, size, background)
                }
                line = line shr 1
            }
        }

        if (background != foreground) {
            if (size == 1) writeFastVLine(x + 5, y, 8, background)
            else writeFillRect(x + 5 * size, y, size, 8 * size, background)
        }
        endWrite()
    }

    fun write(c: Char) {
        if (gfxFont == null) {
            if (c == '\n') {
                cursorX = 0
                cursorY += textSize * 8
            } else if (c != '\r') {
                if (wrap && ((cursorX + textSize * 6) > width)) {
                    cursorX = 0
                    cursorY += textSize * 8
                }
                drawChar(cursorX, cursorY, c, textColor, textBg, textSize)
                cursorX += textSize * 6
            }

        } else {
            val cValue = c.toInt()
            if (c == '\n') {
                cursorX = 0
                cursorY += textSize * gfxFont!!.yAdvance
            } else if (c != '\r') {
                val first = gfxFont!!.first
                val last = gfxFont!!.last
                if ((cValue >= first) && (cValue <= last)) {
                    val glyph = gfxFont!!.glyph[cValue - first]
                    val w = glyph.width
                    val h = glyph.height
                    if ((w > 0) && (h > 0)) {
                        val xo = glyph.xOffset
                        if (wrap && ((cursorX + textSize * (xo + w)) > width)) {
                            cursorX = 0
                            cursorY += textSize * gfxFont!!.yAdvance
                        }
                        drawChar(cursorX, cursorY, c, textColor, textBg, textSize)
                    }
                    cursorX += glyph.xAdvance * textSize
                }
            }

        }
    }

    fun write(str: String) {
        val sanitizedString = str.stripNonPrintable()
        for (char in sanitizedString) write(char)
    }

    fun drawXBitmap(x: Int, yIn: Int, bitmap: IntArray, w: Int, h: Int, color: Color) {
        // Nearly identical to drawBitmap(), only the bit order
        // is reversed here (left-to-right = LSB to MSB):
        drawBitmapMSB(x, yIn, bitmap, w, h) { pixelX, pixelY, currentByte ->
            if (isMsbSet(currentByte)) writePixel(pixelX, pixelY, color)
        }
    }

    fun drawBitmapInverted(x: Int, yIn: Int, bitmap: IntArray, w: Int, h: Int, color: Color) {
        drawBitmapLSB(x, yIn, bitmap, w, h) { pixelX, pixelY, currentByte ->
            if (!isLsbSet(currentByte)) writePixel(pixelX, pixelY, color)
        }
    }

    fun drawBitmap(x: Int, yIn: Int, bitmap: IntArray, w: Int, h: Int, color: Color) {
        drawBitmapLSB(x, yIn, bitmap, w, h) { pixelX, pixelY, currentByte ->
            if (isLsbSet(currentByte)) writePixel(pixelX, pixelY, color)
        }
    }

    fun drawBitmap(x: Int, yIn: Int, bitmap: IntArray, w: Int, h: Int, color: Color, bg: Color) {
        drawBitmapLSB(x, yIn, bitmap, w, h) { pixelX, pixelY, currentByte ->
            val pixelColor = when(isLsbSet(currentByte)) {
                true -> color
                false -> bg
            }
            writePixel(pixelX, pixelY, pixelColor)
        }
    }

    private fun drawBitmapLSB(x: Int, y: Int, bitmap: IntArray, w: Int, h: Int, pixelRenderer: (pixelX: Int, pixelY: Int, currentByte: Byte) -> Unit) {
        drawBitmap(x, y, w, h, pixelRenderer) { column: Int, line: Int, currentByte: Int, bytesPerLinePadded: Int ->
            if (column and 0x07 != 0) currentByte shl 1
            else bitmap[line * bytesPerLinePadded + column / 8]
        }
    }

    private fun drawBitmapMSB(x: Int, y: Int, bitmap: IntArray, w: Int, h: Int, pixelRenderer: (pixelX: Int, pixelY: Int, currentByte: Byte) -> Unit) {
        drawBitmap(x, y, w, h, pixelRenderer) { column: Int, line: Int, currentByte: Int, bytesPerLinePadded: Int ->
            if (column and 7 != 0) currentByte shr 1
            else bitmap[line * bytesPerLinePadded + column / 8]
        }
    }

    fun drawBitmap(
            x: Int,
            y: Int,
            w: Int,
            h: Int,
            pixelRenderer: (pixelX: Int, pixelY: Int, currentByte: Byte) -> Unit,
            byteSource: (column: Int, line: Int, currentByte: Int, bytesPerLinePadded: Int) -> Int
    ) {
        val bytesPerLinePadded = calculateBytesPerLineWithPadding(w)
        var currentY = y

        startWrite()
        for (line in 0 until h) {
            var byte = 0
            for (column in 0 until w) {
                byte = byteSource.invoke(column, line, byte, bytesPerLinePadded)

                val currentX = x + column
                pixelRenderer.invoke(currentX, currentY, byte.toByte())
            }
            currentY++
        }
        endWrite()
    }

    protected enum class Corner {
        BOTTOM_RIGHT, TOP_RIGHT, BOTTOM_LEFT, TOP_LEFT
    }
}

private fun isLsbSet(value: Byte) = (value and 0x80.toByte()) == 0x80.toByte()

private fun isMsbSet(value: Byte) = (value and 0x01.toByte()) == 0x01.toByte()

private fun calculateBytesPerLineWithPadding(pixelWidth: Int): Int = (pixelWidth + 7) / 8

fun String.stripNonPrintable(): String {
    val sb = StringBuilder()
    for (c in this) {
        val i = c.toInt()
        if (i in 32..126 || i >= 128 || i == '\n'.toInt() || i == '\r'.toInt()) sb.append(c)
    }
    return sb.toString()
}
