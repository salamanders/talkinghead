package info.benjaminhill.scriptgen

import org.junit.jupiter.api.Test

class StrokeTest {
    @Test
    fun runImageToStrokesLiberty() = runDrawTest(ImageToStrokes(fileName = "images/liberty.png"), "liberty")

    @Test
    fun runImageToStrokesSW() = runDrawTest(ImageToStrokes(fileName = "images/sw.png"), "sw")

    @Test
    fun runImageToStrokesShark() = runDrawTest(ImageToStrokes(fileName = "images/shark.png"), "shark")
}