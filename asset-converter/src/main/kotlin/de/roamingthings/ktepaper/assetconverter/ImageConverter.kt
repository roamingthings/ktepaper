package de.roamingthings.ktepaper.assetconverter

import javafx.embed.swing.SwingFXUtils
import javafx.scene.image.Image
import org.springframework.stereotype.Component
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp
import java.util.stream.Collectors
import java.util.stream.IntStream

@Component
class ImageConverter {
    fun createSourceForImage(image: BufferedImage): String {
        val imageDataBuffer = image.raster.dataBuffer

        val elementTransformer = Transformer(8)

        return IntStream.range(0, imageDataBuffer.size)
                .mapToObj { imageDataBuffer.getElem(it) }
                .map { elementTransformer.transformElement(it) }
                .collect(Collectors.joining(", ", "intArrayOf(", ")"))
    }

    public fun convertToBitmap(image: Image, targetWidth: Int, targetHeight: Int): BufferedImage {
        val inputImage = SwingFXUtils.fromFXImage(image, null)
        val scaledImage = scaleImage(inputImage, targetWidth, targetHeight)
        val blackWhite = convertToMonochromeImage(scaledImage)
        return blackWhite
    }

    public fun convertToMonochromeImage(scaledImage: BufferedImage): BufferedImage {
        val blackWhite = BufferedImage(scaledImage.getWidth(), scaledImage.getHeight(), BufferedImage.TYPE_BYTE_BINARY)
        val op = ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null)
        op.filter(scaledImage, blackWhite)
        return blackWhite
    }

    public fun scaleImage(inputImage: BufferedImage, width: Int, height: Int): BufferedImage {
        // creates output image
        val scaledImage = BufferedImage(width, height, inputImage.getType())

        // scales the input image to the output image
        val g2d = scaledImage.createGraphics()
        g2d.drawImage(inputImage, 0, 0, width, height, null)
        g2d.dispose()
        return scaledImage
    }

    private class Transformer(private val numElementsPerLine: Int) {
        private var currentElement = 0

        fun transformElement(element: Int): String {
            val result = StringBuilder()
            if (++currentElement % numElementsPerLine == 0) {
                result.append("\n")
            }

            result.append("0x")
            if (element < 0x10) {
                result.append("0")
            }
            result.append(Integer.toString(element, 16).toUpperCase())
            return result.toString()
        }
    }
}