package info.benjaminhill.scriptgen.util

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import java.awt.Rectangle
import kotlin.math.cos
import kotlin.math.sin

operator fun Vector2D.component1(): Double = this.x
operator fun Vector2D.component2(): Double = this.y

fun angleToVector2D(rad: Double) = Vector2D(cos(rad), sin(rad))

fun Rectangle.contains(v: Vector2D): Boolean = contains(v.x.toInt(), v.y.toInt())




