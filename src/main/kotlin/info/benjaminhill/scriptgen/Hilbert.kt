package info.benjaminhill.scriptgen

import info.benjaminhill.scriptgen.util.*
import info.benjaminhill.scriptgen.util.NormalVector2D.Companion.normalOrNull
import mu.KotlinLogging
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Squiggles get smaller in darker ink areas.
 *
 * https://en.wikipedia.org/wiki/Moore_curve
 *
 * Alphabet: L, R, Constants: F, +, −
 * Axiom: LFL+F+LFL
 * Production rules:
 * L → −RF+LFL+FR−
 * R → +LF−RFR−FL+
 * Here, F means "draw forward", − means "turn left 90°", and + means "turn right 90°"
 */
class Hilbert(fileName: String, private val maxDepth: Int) : AbstractDrawing(fileName) {

    private var headingDeg = 0
    private var location = Vector2D(0.0, 0.0)
    private val locationHistory = mutableListOf<Vector2D>()

    private var hopSize = 1.0


    /** Axiom: LFL+F+LFL */
    override fun generateScript(): Script {
        doA(maxDepth)
        val normalizedLocationHistory = NormalVector2D.normalizePoints(locationHistory)
        hopSize = normalizedLocationHistory[0].distance(normalizedLocationHistory[1])

        val normalScriptWithX = mutableScriptOf()
        normalizedLocationHistory.forEach { loc ->
            normalScriptWithX.add(loc)
            normalScriptWithX.addAll(drawX(loc))
            normalScriptWithX.add(loc)
        }

        LOG.info { "With all squiggles: ${normalScriptWithX.size}" }
        val noDupes = normalScriptWithX.removeDuplicates()
        LOG.info { "noDupes: ${noDupes.size}" }
        return noDupes
    }

    private fun drawX(loc: NormalVector2D): MutableList<NormalVector2D> {
        val result: MutableScript = mutableScriptOf()
        val ink = sfi.getInk(loc)
        val xLegSize = sqrt(hopSize * hopSize + hopSize * hopSize)
        listOf(-1, 1).forEach { x ->
            listOf(-1, 1).forEach { y ->
                val squiggled = loc.add(Vector2D(x * ink * xLegSize, y * ink * xLegSize))
                squiggled.normalOrNull()?.let {
                    result.add(it)
                }
            }
        }
        return result
    }

    private fun execute(c: Char, depth: Int) {
        if (depth <= 0) {
            return
        }
        when (c) {
            'L' -> doL(depth)
            'R' -> doR(depth)
            'F' -> doF()
            '-' -> headingDeg -= 90
            '+' -> headingDeg += 90
            else -> throw IllegalArgumentException("$c")
        }
    }

    /** Axiom */
    private fun doA(depth: Int) {
        "LFL+F+LFL".forEach { c ->
            execute(c, depth)
        }
    }


    private fun doL(depth: Int) {
        "-RF+LFL+FR-".forEach { c ->
            execute(c, depth - 1)
        }
    }

    private fun doR(depth: Int) {
        "+LF-RFR-FL+".forEach { c ->
            execute(c, depth - 1)
        }
    }

    private fun doF() {
        val rad = Math.toRadians(headingDeg.toDouble())
        location = location.add(Vector2D(cos(rad), sin(rad)))
        //LOG.info { "$location -> $nextLocation" }
        locationHistory.add(location)
    }

    companion object {
        val LOG = KotlinLogging.logger {}
    }
}