package de.roamingthings.ktgfx

class GfxFont(
        val bitmap: IntArray,      // Glyph bitmaps, concatenated
        val glyph: Array<GfxGlyph>,       // Glyph array
        val first: Int,
        val last: Int, // ASCII extents
        val yAdvance: Int
)

class GfxGlyph(
        val bitmapOffset: Int,     // Pointer into GFXfont->bitmap
        val width: Int,
        val height: Int,    // Bitmap dimensions in pixels
        val xAdvance: Int,         // Distance to advance cursor (x axis)
        val xOffset: Int,
        val yOffset: Int
)