package de.roamingthings.ktepaper.assetconverter

import de.roamingthings.ktepaper.assetconverter.view.MasterView
import de.roamingthings.ktepaper.assetconverter.view.Styles
import javafx.application.Application
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.ComponentScan
import tornadofx.*
import kotlin.reflect.KClass

@SpringBootApplication
@ComponentScan
class AssetConverterApplication : App(MasterView::class, Styles::class) {

    override fun init() {
        reloadStylesheetsOnFocus()

        val applicationContext = SpringApplication.run(this.javaClass, *parameters.raw.toTypedArray())
        applicationContext.autowireCapableBeanFactory.autowireBean(this)

        FX.dicontainer = object : DIContainer {
            override fun <T : Any> getInstance(type: KClass<T>): T = applicationContext.getBean(type.java)
            override fun <T : Any> getInstance(type: KClass<T>, name: String): T = applicationContext.getBean(type.java, name)
        }
        super.init()
    }
}

fun main(args: Array<String>) {
    Application.launch(AssetConverterApplication::class.java, *args)
}
