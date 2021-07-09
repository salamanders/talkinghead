package info.benjaminhill.scriptgen

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import java.awt.Rectangle
import java.awt.geom.Line2D
import java.awt.geom.Point2D
import java.awt.image.BufferedImage
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

operator fun Vector2D.component1(): Double = this.x
operator fun Vector2D.component2(): Double = this.y

fun angleToVector2D(rad: Double) = Vector2D(cos(rad), sin(rad))

fun Rectangle.contains(v: Vector2D): Boolean = contains(v.x.toInt(), v.y.toInt())

fun BufferedImage.getLum(x: Int, y: Int): Float {
    require(x in 0 until width) { "x:$x outside of $width x $height" }
    require(y in 0 until height) { "y:$y outside of $width x $height" }
    val color = getRGB(x, y)
    val red = color.ushr(16) and 0xFF
    val green = color.ushr(8) and 0xFF
    val blue = color.ushr(0) and 0xFF
    return (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255
}

/**
 * Bresenham's algorithm to find all pixels on a Line2D.
 * @author nes
 * from https://snippets.siftie.com/nikoschwarz/iterate-all-points-on-a-line-using-bresenhams-algorithm/
 */
fun Line2D.pixelsOnLine(): List<Point2D> {
    val precision = 1.0
    val sx: Double = if (x1 < x2) precision else -1 * precision
    val sy: Double = if (y1 < y2) precision else -1 * precision
    val dx: Double = abs(x2 - x1)
    val dy: Double = abs(y2 - y1)

    var x: Double = x1
    var y: Double = y1
    var error: Double = dx - dy

    val result = mutableListOf<Point2D>()

    while (abs(x - x2) > 0.9 || abs(y - y2) > 0.9) {
        val ret = Point2D.Double(x, y)

        val e2 = 2 * error
        if (e2 > -dy) {
            error -= dy
            x += sx
        }
        if (e2 < dx) {
            error += dx
            y += sy
        }

        result.add(ret)
    }
    return result
}
