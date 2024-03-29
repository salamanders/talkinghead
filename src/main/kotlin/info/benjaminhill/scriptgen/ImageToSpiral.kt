package info.benjaminhill.scriptgen

import info.benjaminhill.scriptgen.util.NormalVector2D
import info.benjaminhill.scriptgen.util.NormalVector2D.Companion.normalOrNull
import info.benjaminhill.scriptgen.util.Script
import info.benjaminhill.scriptgen.util.mutableScriptOf
import mu.KotlinLogging
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import kotlin.math.cos
import kotlin.math.sin

/**
 * Spiral line, squiggle the line in dark areas
 */
class ImageToSpiral(fileName: String, private val numberOfSpins: Int) : AbstractDrawing(fileName) {

    override fun generateScript(): Script {
        val result = mutableScriptOf()

        val res = 500 // for compression removing identical spiral points
        val center = NormalVector2D(.5, .5)

        val spiralPoints = unitSpiral(numberOfSpins).map {
            it.add(center)
        }.mapNotNull {
            it.normalOrNull()
        }.distinctBy { (it.x * res).toInt() to (it.y * res).toInt() }

        val spaceBetweenSpins = center.norm / numberOfSpins
        LOG.info { "Plotting ${spiralPoints.size} points, with gap $spaceBetweenSpins" }

        // Do the spiral, jog to center when you find darkness
        spiralPoints.forEach { a ->
            // "Real" would be to average pixel lum in the little pie slice.  Meh.
            result.add(a)
            val ink = sfi.getInk(a)
            if (ink > .01) {
                val squiggleSize = spaceBetweenSpins * ink * 1.5
                val squiggle = a.subtract(NormalVector2D(.5, .5)).normalize().scalarMultiply(squiggleSize).add(a)
                squiggle.normalOrNull()?.let {
                    result.add(it)
                }
            }

        }
        return result
    }

    companion object {
        /**
         * -1..1
         */
        private fun unitSpiral(numberOfSpins: Int): List<Vector2D> {
            val radiusIncreasePerSpin = 1.0 / numberOfSpins
            return (1 until numberOfSpins * 360).map { deg ->
                val spinPct = deg / 360.0
                val radius = spinPct * radiusIncreasePerSpin
                val rad = Math.toRadians(deg.toDouble())
                Vector2D(cos(rad), sin(rad)).scalarMultiply(radius)
            }
        }


        val LOG = KotlinLogging.logger {}

    }
}