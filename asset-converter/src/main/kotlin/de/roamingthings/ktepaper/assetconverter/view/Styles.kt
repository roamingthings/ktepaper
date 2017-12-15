package de.roamingthings.ktepaper.assetconverter.view

import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        // Define our styles
        val wrapper by cssclass()
        val previewImage by cssclass()

        // Define our colors
        val dangerColor = c("#a94442")
        val hoverColor = c("#d49942")

    }

    init {
        wrapper {
            padding = box(10.px)
            spacing = 10.px
        }

        previewImage {
            borderWidth += box(1.px)
            borderColor += box(Color.BLACK)
            borderStyle += BorderStrokeStyle.SOLID
        }
    }
}