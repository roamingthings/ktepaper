package de.roamingthings.ktepaper.assetconverter.view

import de.roamingthings.ktepaper.assetconverter.image.view.ImageConversionView
import tornadofx.*

class MasterView : View() {

    override val root = vbox {
        addClass(Styles.wrapper)

        add(ImageConversionView::class)
    }
}