package info.benjaminhill.scriptgen

import org.junit.jupiter.api.Test

class SpiralTest {
    @Test
    fun runImageToSpiral() = runDrawTest(ImageToSpiral(fileName = "images/liberty.png", numberOfSpins = 100), "liberty")

}