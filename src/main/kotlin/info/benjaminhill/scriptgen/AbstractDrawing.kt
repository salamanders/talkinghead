package info.benjaminhill.scriptgen

import info.benjaminhill.scriptgen.util.*
import info.benjaminhill.scriptgen.util.ScaleFreeImage.Companion.toScaleFreeImage
import info.benjaminhill.utils.getFile
import java.io.File
import javax.imageio.ImageIO

/**
 * Load up an image, convert it to a scale free image, and (lazy) create and export a script
 */
abstract class AbstractDrawing(fileName: String) {
    protected val sfi: ScaleFreeImage = ImageIO.read(getFile(fileName)).toScaleFreeImage()

    private val script: Script by lazy {
        generateScript()
    }

    internal abstract fun generateScript(): Script

    fun exportToImage(outputFile: File) {
        ImageIO.write(script.toImage(), "png", outputFile)
    }

    fun exportToScript(outputFile: File) {
        outputFile.writeText(script.removeDuplicates().toText())
    }
}