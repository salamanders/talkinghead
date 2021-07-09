package info.benjaminhill.scriptgen.util

import info.benjaminhill.scriptgen.getLum
import java.awt.image.BufferedImage

/**
 * A square scale free image of lum values
 * All access to the input and output images are done in a normalized manner (0.0 until 1.0)
 * Used for converting images into drawing commands.
 * Resolution is maintained from the original image.
 */
class ScaleFreeImage
private constructor(
    private val inputDimension: Int,
    private val inputImageInk: FloatArray,
) {
    fun getInk(a: NormalVector2D): Float {
        val xa = (a.x * inputDimension).toInt()
        val ya = (a.y * inputDimension).toInt()
        val idx = ya * inputDimension + xa
        return if (idx < inputImageInk.size) inputImageInk[idx] else 0f
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

