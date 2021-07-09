package info.benjaminhill.scriptgen

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

internal class ScaleFreeTest {

    @Test
    fun runHilbert() = runDrawTest(Hilbert(fileName = "images/liberty.png", maxDepth = 7))

    @Test
    fun runImageToScan() = runDrawTest(ImageToScan(fileName = "images/xwingl.png"))

    @Test
    fun runImageToSpiral() = runDrawTest(ImageToSpiral(fileName = "images/liberty.png", numberOfSpins = 100))
}


private fun runDrawTest(wrappedDrawer: AbstractDrawing) {
    val name = wrappedDrawer.javaClass.simpleName

    File("scriptgen/output_$name.png").also {
        it.delete() // cleanup leftovers, but we want the output to stick around, so we can look at it
        wrappedDrawer.exportToImage(it)
        Assertions.assertTrue(it.exists(), "Output image file missing: '${it.canonicalPath}'")
        Assertions.assertTrue(it.length() > 0, "Output image file empty: '${it.canonicalPath}'")
    }

    File("scriptgen/output_$name.txt").also {
        it.delete()
        wrappedDrawer.exportToScript(it)
        Assertions.assertTrue(it.exists(), "Output text file missing: '${it.canonicalPath}'")
        Assertions.assertTrue(it.length() > 0, "Output text file empty: '${it.canonicalPath}'")
    }
}