package de.roamingthings.ktepaper.assetconverter.image.view

import de.roamingthings.ktepaper.assetconverter.ImageConverter
import de.roamingthings.ktepaper.assetconverter.view.Styles
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingFXUtils.toFXImage
import javafx.geometry.Pos.CENTER
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority.ALWAYS
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

private const val displayWidth = 176.0
private const val displayHeight = 264.0

class ImageConversionView : View() {

    private val imageConverter: ImageConverter by di()

    private lateinit var sourceImagePathField: TextField
    private lateinit var originalImageView: ImageView
    private lateinit var convertedImageView: ImageView

    private val originalImageLocation = SimpleStringProperty()
    private val imageSource = SimpleStringProperty()

    override val root = vbox(10) {
        useMaxSize = true

        text("Convert Image") {
            padding = insets(6.0)
            useMaxWidth = true
        }

        hbox(10) {
            label("Source Image") {
                alignment = CENTER
                useMaxHeight = true
            }
            sourceImagePathField = textfield {
                hgrow = ALWAYS
                useMaxWidth = true

                bind(originalImageLocation)
                originalImageLocation.addListener({ observable, oldValue, newValue ->
                    loadImage(newValue)
                })
            }
            button("...") {
                action {
                    val filters = arrayOf(FileChooser.ExtensionFilter("Image files", "*.bmp", "*.jpg", "*.png", "*.gif"))
                    val chosenFiles = chooseFile("Select some text files", filters)
                    if (chosenFiles.isNotEmpty()) {
                        val newImageFile = chosenFiles[0].absolutePath
                        originalImageLocation.set(newImageFile)
                    }
                }
            }
        }
        hbox(10) {
            vbox {
                originalImageView = imageview {
                    addClass(Styles.previewImage)
                    prefWidth = displayWidth
                    prefHeight = displayHeight
                    fitWidth = displayWidth
                    fitHeight = displayHeight
                }
                label("Original Image") {
                    useMaxWidth = true
                    alignment = CENTER
                }
            }
            label(">>") {
                hgrow = ALWAYS
                alignment = CENTER
                useMaxSize = true
            }
            vbox {
                convertedImageView = imageview {
                    addClass(Styles.previewImage)
                    prefWidth = displayWidth
                    prefHeight = displayHeight
                    fitWidth = displayWidth
                    fitHeight = displayHeight
                }
                text("Monochrome Image") {
                    useMaxWidth = true
                    alignment = CENTER
                }
            }
        }
        textarea {
            prefWidth = 600.0
            prefHeight = 400.0
            useMaxSize = true
            vgrow = ALWAYS
            hgrow = ALWAYS
            bind(imageSource)
        }
        hbox {
            useMaxWidth = true
            hgrow = ALWAYS
            vbox {
                useMaxWidth = true
                hgrow = ALWAYS
            }
            button("Copy to clipboard") {
                action {
                    clipboard.setContent {
                        putString(imageSource.get())
                    }
                }
            }
        }
    }

    private fun loadImage(imagePath: String) {
        val imageFile = File(imagePath)
        if (imageFile.exists()) {
            val image = Image(imageFile.inputStream())
            originalImageView.image = image
            val bitmap = imageConverter.convertToBitmap(image, displayWidth.toInt(), displayHeight.toInt())
            convertedImageView.image = toFXImage(bitmap, null)
            imageSource.set(imageConverter.createSourceForImage(bitmap))
        }
    }

    override fun onDock() {
        if (originalImageLocation.isEmpty.value) {
            val welcomeResource = javaClass.getResource("/welcome.bmp")?.file
            if (welcomeResource != null) {
                originalImageLocation.set(welcomeResource)
            }
        }
    }
}
