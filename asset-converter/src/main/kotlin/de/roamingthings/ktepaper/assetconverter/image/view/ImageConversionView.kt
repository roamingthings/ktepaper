package de.roamingthings.ktepaper.assetconverter.image.view

import de.roamingthings.ktepaper.assetconverter.ImageConverter
import de.roamingthings.ktepaper.assetconverter.view.Styles
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingFXUtils.toFXImage
import javafx.geometry.Pos.CENTER
import javafx.geometry.Pos.CENTER_LEFT
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.Priority.ALWAYS
import javafx.stage.FileChooser
import tornadofx.*
import java.io.File

//private const val displayWidth = 176.0
//private const val displayHeight = 264.0
private const val displayWidth = 128.0
private const val displayHeight = 250.0

class ImageConversionView : View() {

    private val imageConverter: ImageConverter by di()

    private lateinit var sourceImagePathField: TextField
    private lateinit var originalImageView: ImageView
    private lateinit var convertedImageView: ImageView
    private lateinit var targetWidthField: TextField
    private lateinit var targetHeightField: TextField

    private val originalImagePath = SimpleStringProperty()
    private val imageSource = SimpleStringProperty()
    private val targetWidth = SimpleIntegerProperty(displayWidth.toInt())
    private val targetHeight = SimpleIntegerProperty(displayHeight.toInt())

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

                bind(originalImagePath)
                originalImagePath.addListener({ observable, oldValue, newValue ->
                    loadImage(newValue)
                })
            }
            button("...") {
                action {
                    val filters = arrayOf(FileChooser.ExtensionFilter("Image files", "*.bmp", "*.jpg", "*.png", "*.gif"))
                    val chosenFiles = chooseFile("Select some text files", filters)
                    if (chosenFiles.isNotEmpty()) {
                        val newImageFile = chosenFiles[0].absolutePath
                        originalImagePath.set(newImageFile)
                    }
                }
            }
        }
        hbox(10) {
            label("Target width") {
                alignment = CENTER
                useMaxHeight = true
            }
            targetWidthField = textfield {
                bind(targetWidth)
                targetWidth.addListener({ observable, oldValue, newValue ->
                    updateTargetDimension(newValue.toInt(), targetHeight.value)
                    loadImage(originalImagePath.value)
                })
            }
            label("px") {
                alignment = CENTER_LEFT
                useMaxHeight = true
            }
            label("height") {
                alignment = CENTER
                useMaxHeight = true
            }
            targetHeightField = textfield {
                bind(targetHeight)
                targetHeight.addListener({ observable, oldValue, newValue ->
                    updateTargetDimension(targetWidth.value, newValue.toInt())
                    loadImage(originalImagePath.value)
                })
            }
            label("px") {
                alignment = CENTER_LEFT
                hgrow = ALWAYS
                useMaxWidth = true
                useMaxHeight = true
            }
        }

        hbox(10) {
            vbox {
                originalImageView = imageview {
                    addClass(Styles.previewImage)
                    prefWidth = targetWidth.value.toDouble()
                    prefHeight = targetHeight.value.toDouble()
                    fitWidth = targetWidth.value.toDouble()
                    fitHeight = targetHeight.value.toDouble()
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
                    prefWidth = targetWidth.value.toDouble()
                    prefHeight = targetHeight.value.toDouble()
                    fitWidth = targetWidth.value.toDouble()
                    fitHeight = targetHeight.value.toDouble()
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
                        putString(imageSource.value)
                    }
                }
            }
        }
    }

    private fun updateTargetDimension(width: Int, height: Int) {
        convertedImageView.resize(width.toDouble(), height.toDouble())
        convertedImageView.prefHeight(height.toDouble())
        convertedImageView.prefWidth(width.toDouble())
        convertedImageView.fitHeight = height.toDouble()
        convertedImageView.fitWidth = width.toDouble()
    }

    private fun loadImage(imagePath: String) {
        val imageFile = File(imagePath)
        if (imageFile.exists()) {
            val image = Image(imageFile.inputStream())
            originalImageView.image = image
            val bitmap = imageConverter.convertToBitmap(image, targetWidth.value, targetHeight.value)
            convertedImageView.image = toFXImage(bitmap, null)
            imageSource.set(imageConverter.createSourceForImage(bitmap))
        }
    }

    override fun onDock() {
        if (originalImagePath.isEmpty.value) {
            val welcomeResource = javaClass.getResource("/welcome.bmp")?.file
            if (welcomeResource != null) {
                originalImagePath.set(welcomeResource)
            }
        }
    }
}
