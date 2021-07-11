package info.benjaminhill.scriptgen.util

import info.benjaminhill.utils.r
import info.benjaminhill.utils.round
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import kotlin.math.min

/** Extra check to make sure a sane normal vector */
class NormalVector2D(x: Double, y: Double) : Vector2D(x, y) {

    init {
        require(isNormal(this)) { "non normal point: ${this.x} x ${this.y}" }
    }

    override fun toString(): String = "{\"x\":${x.round(4)}, \"y\":${y.round(4)}}"

    // When the result should also be within normal range
    fun addN(v: NormalVector2D): NormalVector2D = super.add(v).asNormalVector2D()

    // When the result should also be within normal range
    fun subtractN(v: NormalVector2D): NormalVector2D = super.subtract(v).asNormalVector2D()

    // When the result should also be within normal range
    fun scalarMultiplyN(a: Double): NormalVector2D = super.scalarMultiply(a).asNormalVector2D()

    fun normalizeN() = normalize().asNormalVector2D()

    /** Get 0 to numPoints along a line using even steps */
    fun getNPointsAlongLine(other: NormalVector2D, numPoints: Int = 20): List<NormalVector2D> {
        val diff = other.subtract(this).normalize()
        val distance = this.distance(other)
        return (0..numPoints).map {
            add((it.toDouble() / numPoints) * distance, diff).asNormalVector2D()
        }
    }


    companion object {

        fun coerceNormalVector2D(x: Double, y: Double): NormalVector2D =
            NormalVector2D(x.coerceIn(0.0, 0.999999999), y.coerceIn(0.0, 0.999999999))

        fun Vector2D.normalOrNull(): NormalVector2D? = normalOrNull(this.x, this.y)

        fun normalOrNull(x: Double, y: Double): NormalVector2D? = try {
            NormalVector2D(x, y)
        } catch (e: IllegalArgumentException) {
            null
        }

        /** A bit more wiggle room because diagonals can extend longer, but shouldn't go shorter */
        fun checkDiagonalsNormal(hypotenuseLeft: Double, hypotenuseRight: Double) {
            check(hypotenuseLeft >= -0.05) { "normalized string unexpected hypotenuseLeft:${hypotenuseLeft.r}" }
            check(hypotenuseLeft < 1.5) { "normalized string unexpected hypotenuseLeft:${hypotenuseLeft.r}" }
            check(hypotenuseRight >= -0.05) { "normalized string unexpected hypotenuseRight:${hypotenuseLeft.r}" }
            check(hypotenuseRight < 1.5) { "normalized string unexpected hypotenuseRight:${hypotenuseRight.r}" }
        }

        /** Also checks to make sure within bounds */
        fun Vector2D.asNormalVector2D(): NormalVector2D {
            // accept border cases by cheating
            val x = if (this.x == 1.0) {
                0.999999999
            } else {
                this.x
            }
            val y = if (this.y == 1.0) {
                0.999999999
            } else {
                this.y
            }
            return NormalVector2D(x, y)
        }

        /** Scale proportionally to fit inside a unit sq, trimming to shape */
        fun normalizePoints(points: List<Vector2D>): List<NormalVector2D> {
            val globalMin = Vector2D(points.minByOrNull { it.x }!!.x, points.minByOrNull { it.y }!!.y)
            val globalMax = Vector2D(points.maxByOrNull { it.x }!!.x, points.maxByOrNull { it.y }!!.y)

            val xScale = 1 / (globalMax.x - globalMin.x)
            val yScale = 1 / (globalMax.y - globalMin.y)
            val scaleFactor = min(xScale, yScale)

            val scaled = points.map {
                it.subtract(globalMin).scalarMultiply(scaleFactor).asNormalVector2D()
            }

            val scaledMax = NormalVector2D(scaled.maxByOrNull { it.x }!!.x, scaled.maxByOrNull { it.y }!!.y)
            val centeringOffset = NormalVector2D((1 - scaledMax.x) / 2, (1 - scaledMax.y) / 2)

            return scaled.map {
                it.addN(centeringOffset)
            }
        }

        private fun isNormal(p: Vector2D) = isNormal(p.x, p.y)

        private fun isNormal(x: Double, y: Double) =
            x.isFinite() &&
                    y.isFinite() &&
                    x >= 0 &&
                    x < 1 &&
                    y >= 0 &&
                    y < 1
    }
}