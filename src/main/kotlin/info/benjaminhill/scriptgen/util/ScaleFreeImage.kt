package info.benjaminhill.scriptgen.util

import info.benjaminhill.scriptgen.util.NormalVector2D.Companion.normalOrNull
import java.awt.image.BufferedImage
import kotlin.math.abs

/**
 * A square scale free image of lum values
 * All access to the input and output images are done in a normalized manner (0.0 until 1.0)
 * Used for converting images into drawing commands.
 * Resolution is maintained from the original image.
 * @param inputDimension the max res (width or height) of input image
 */
class ScaleFreeImage
private constructor(
    private val inputDimension: Int,
    private val inputImageInk: FloatArray,
) {
    private fun pointToIndex(point: NormalVector2D): Int {
        val x = (point.x * inputDimension).toInt()
        val y = (point.y * inputDimension).toInt()
        return y * inputDimension + x
    }

    /** Scale up by the image dimension then sample from the backing array */
    fun getInk(point: NormalVector2D): Float = inputImageInk[pointToIndex(point)]

    private fun setInk(point: NormalVector2D, ink: Float = 0f) {
        inputImageInk[pointToIndex(point)] = ink
    }

    fun whiteout(p1: NormalVector2D, p2: NormalVector2D) {
        pixelsOnLine(p1, p2).forEach { (point, _) ->
            setInk(point, 0f)
        }
    }

    fun avgInk(p1: NormalVector2D, p2: NormalVector2D) = pixelsOnLine(p1, p2)
        .map { (_, lum) ->
            lum * lum
        }.average()

    /**
     * Bresenham's algorithm to find all pixels on a Line2D. Assuming pixel grid is every unit.
     * @author nes
     * from https://snippets.siftie.com/nikoschwarz/iterate-all-points-on-a-line-using-bresenhams-algorithm/
     * @return point plus lum of each pixel on the line
     */
    private fun pixelsOnLine(p1: NormalVector2D, p2: NormalVector2D): List<Pair<NormalVector2D, Float>> {
        val precision = 1.0
        val x1 = p1.x * inputDimension
        val y1 = p1.y * inputDimension
        val x2 = p2.x * inputDimension
        val y2 = p2.y * inputDimension
        val sx: Double = if (x1 < x2) precision else -1 * precision
        val sy: Double = if (y1 < y2) precision else -1 * precision
        val dx: Double = abs(x2 - x1)
        val dy: Double = abs(y2 - y1)

        var x: Double = x1
        var y: Double = y1
        var error: Double = dx - dy

        val result = mutableListOf<Pair<Double, Double>>()

        while (abs(x - x2) > 0.9 || abs(y - y2) > 0.9) {
            val ret = Pair(x, y)

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
            .mapNotNull { point ->
                normalOrNull(point.first / inputDimension, point.second / inputDimension)
            }
            .map { nPoint ->
                val ink = getInk(nPoint)
                nPoint to ink
            }
    }

    companion object {
        /** Centers the image and scales it down uniformly to a 1x1 max */
        fun BufferedImage.toScaleFreeImage(): ScaleFreeImage {
            val inputDimension: Int

            val (xScoot, yScoot) = if (this.width > this.height) {
                inputDimension = this.width
                Pair(0, (this.width - this.height) / 2)
            } else {
                inputDimension = this.height
                Pair((this.height - this.width) / 2, 0)
            }
            val inputImageInk = FloatArray(inputDimension * inputDimension) { 0f }

            for (x in 0 until this.width) {
                for (y in 0 until this.height) {
                    val actualX = x + xScoot
                    val actualY = y + yScoot
                    inputImageInk[actualY * inputDimension + actualX] = 1 - this.getLum(x, y)
                }
            }
            return ScaleFreeImage(inputDimension, inputImageInk)
        }
    }
}

