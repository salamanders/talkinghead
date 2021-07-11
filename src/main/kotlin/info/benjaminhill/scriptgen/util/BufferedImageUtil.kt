package info.benjaminhill.scriptgen.util

import java.awt.image.BufferedImage

fun BufferedImage.getLum(x: Int, y: Int): Float {
    require(x in 0 until width) { "x:$x outside of $width x $height" }
    require(y in 0 until height) { "y:$y outside of $width x $height" }
    val color = getRGB(x, y)
    val red = color.ushr(16) and 0xFF
    val green = color.ushr(8) and 0xFF
    val blue = color.ushr(0) and 0xFF
    return (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255
}