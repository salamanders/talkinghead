package info.benjaminhill.scriptgen

import info.benjaminhill.scriptgen.util.NormalVector2D
import info.benjaminhill.scriptgen.util.NormalVector2D.Companion.normalOrNull
import info.benjaminhill.scriptgen.util.Script
import info.benjaminhill.scriptgen.util.mutableScriptOf
import mu.KotlinLogging
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs

/**
 * Decimate an image by drawing white lines over it.
 * Each white line is the "most beneficial" next step (based on dark luminosity removed)
 * Is input image scale dependent
 * Can be trapped in a local minimum, but that is ok.
 */
class ImageToStrokes(
    fileName: String,
    private val strokes: Int = 2_000,
    private val searchSteps: Int = 5_000,
    private val maxPctHop: Double = 0.3,
    private val minPctHop: Double = 0.0001,
) : AbstractDrawing(fileName) {

    override fun generateScript(): Script {
        // Seed at center
        val resultScript = mutableScriptOf(NormalVector2D(.5, .5))

        for (i in 0..strokes) {
            if (i % 100 == 0) {
                LOG.info { "$i of $strokes" }
            }
            val currentLocation = resultScript.last()
            val allPossibleNext: List<Pair<NormalVector2D, Double>> = (0..searchSteps).mapNotNull {
                getRandomLocation(currentLocation)
            }
            if (allPossibleNext.size.toDouble() / searchSteps < .1) {
                LOG.warn { "Excluded too many possible next steps: ${allPossibleNext.size}" }
            }
            if (allPossibleNext.isEmpty()) {
                LOG.warn { "Had a fully empty possible next step list, likely a bug." }
            }
            val (bestPt: NormalVector2D, _) = allPossibleNext.maxByOrNull { it.second }!!
            // White-out the current move
            sfi.whiteout(currentLocation, bestPt)
            resultScript.add(bestPt)
        }
        return resultScript
    }

    private fun getRandomLocation(origin: NormalVector2D): Pair<NormalVector2D, Double>? {
        // Gaussian random hops.  This would be a good swarm-optimizer!
        val r1 = ThreadLocalRandom.current().nextGaussian() * maxPctHop
        val r2 = ThreadLocalRandom.current().nextGaussian() * maxPctHop
        if (abs(r1) + abs(r2) < minPctHop) {
            //LOG.info { "Very small hop, bailing. $r1 $r2"}
            return null
        }
        val nextStroke = Vector2D(r1, r2)
        val potentialNextPoint: NormalVector2D = origin.add(nextStroke).normalOrNull() ?: return null

        // too short a hop
        if (origin.distance(potentialNextPoint) < minPctHop) {
            //LOG.info { "Too small a hop, not great. (${origin.distance(potentialNextPoint)})" }
            return null
        }
        val avgInk = sfi.avgInk(origin, potentialNextPoint)
        return Pair(potentialNextPoint, avgInk)
    }

    companion object {
        val LOG = KotlinLogging.logger {}
    }
}

