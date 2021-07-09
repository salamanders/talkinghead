package info.benjaminhill.scriptgen.TODO

import info.benjaminhill.utils.getFile
import kotlinx.coroutines.asCoroutineDispatcher
import mu.KotlinLogging
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Rectangle
import java.awt.RenderingHints
import java.util.concurrent.Executors
import javax.imageio.ImageIO

abstract class AbstractImageToX(fileName: String) : Runnable, AutoCloseable {
    protected val dispatcher =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).asCoroutineDispatcher()
    protected val inputBi = ImageIO.read(getFile(fileName))!!
    protected val inputDim = Rectangle(inputBi.width, inputBi.height)
    protected val inputG2D = inputBi.createGraphics()!!.apply {
        setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        color = Color.WHITE
        stroke = BasicStroke(1f)  // a few mm wide pen?
    }
    protected val script = mutableListOf<Vector2D>()

    abstract fun getNextLocation(origin: Vector2D): Vector2D?

    override fun close() {
        inputG2D.dispose()
        val name = this.javaClass.simpleName


        dispatcher.close()
    }

    companion object {
        val LOG = KotlinLogging.logger {}
    }
}