package de.roamingthings.ktepaper.assetconverter.image.view

import de.roamingthings.ktepaper.assetconverter.view.Styles
import javafx.geometry.Pos.CENTER
import javafx.scene.control.TextField
import javafx.scene.layout.Priority.ALWAYS
import javafx.stage.FileChooser
import tornadofx.*

class ImageConversionView : View() {

    //    val model = ImageConversionModel()
    lateinit var textfield: TextField

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
            textfield = textfield() {
                hgrow = ALWAYS
                useMaxWidth = true
            }
            button("...") {
                action {
                    val filters = arrayOf(FileChooser.ExtensionFilter("Image files", "*.bmp", "*.jpg", "*.png", "*.gif"))
                    val chosenFiles = chooseFile("Select some text files", filters)
                    if (chosenFiles.isNotEmpty()) {
                        val newImageFile = chosenFiles[0].absolutePath
                        textfield.text = newImageFile
                    }
                }
            }
        }
        hbox(10) {
            vbox {
                imageview("welcome.bmp") {
                    addClass(Styles.previewImage)
                    prefWidth = 176.0
                    prefHeight = 264.0
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
                imageview("welcome.bmp") {
                    addClass(Styles.previewImage)
                    prefWidth = 176.0
                    prefHeight = 264.0
                }
                text("Monochrome Image") {
                    useMaxWidth = true
                    alignment = CENTER
                }
            }
        }
        textarea {
            prefWidth = 400.0
            prefHeight = 400.0
            useMaxSize = true
            vgrow = ALWAYS
            hgrow = ALWAYS
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
                    println("copy to clipboard")
                }
            }
        }
    }
}
