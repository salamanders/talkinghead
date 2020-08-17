package info.benjaminhill.bot


import au.edu.federation.caliko.FabrikBone2D
import au.edu.federation.caliko.FabrikChain2D
import au.edu.federation.caliko.FabrikStructure2D
import au.edu.federation.utils.Vec2f
import ev3dev.actuators.lego.motors.BaseRegulatedMotor
import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor
import ev3dev.sensors.ev3.EV3TouchSensor
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import lejos.hardware.port.MotorPort
import lejos.hardware.port.SensorPort
import mu.KotlinLogging
import org.slf4j.impl.SimpleLogger
import kotlin.time.ExperimentalTime


/**
 * All distances are in Lego Studs (~8mm)
 */
internal val logger = KotlinLogging.logger {}

internal const val SHOULDER = 7f

@FlowPreview
@ExperimentalCoroutinesApi
@ExperimentalTime
fun main() {

    System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE")
    println("APP:START")
    println("Debug Enabled: ${logger.isDebugEnabled}")

    val leftShoulder = EV3LargeRegulatedMotor(MotorPort.B)
    val rightShoulder = EV3LargeRegulatedMotor(MotorPort.C)

    // Calibration
    runBlocking(Dispatchers.Default) {
        val (leftRange, rightRange) = getRanges(leftShoulder, rightShoulder)
        println("Left Range: $leftRange")
        println("Right Range: $rightRange")
    }

    val structure = FabrikStructure2D()

    val armLeft = FabrikChain2D().apply {
        setFixedBaseMode(true)
        baseboneConstraintType = FabrikChain2D.BaseboneConstraintType2D.GLOBAL_ABSOLUTE
        baseboneConstraintUV = UP
        addBone(FabrikBone2D(Vec2f(-1 * SHOULDER, 0f), Vec2f(-1 * SHOULDER, 13f)).apply {
            clockwiseConstraintDegs = 80f
            anticlockwiseConstraintDegs = 160f
        })
        addConsecutiveConstrainedBone(UP, 24f, 160f, 10f)
    }

    val armRight = FabrikChain2D().apply {
        setFixedBaseMode(true)
        baseboneConstraintType = FabrikChain2D.BaseboneConstraintType2D.GLOBAL_ABSOLUTE
        baseboneConstraintUV = UP
        addBone(FabrikBone2D(Vec2f(SHOULDER, 0f), Vec2f(SHOULDER, 13f)).apply {
            clockwiseConstraintDegs = 160f
            anticlockwiseConstraintDegs = 80f
        })
        addConsecutiveConstrainedBone(UP, 24f, 10f, 160f)
    }

    structure.addChain(armLeft)
    structure.addChain(armRight)

    val target = Vec2f(-1 * SHOULDER - 13f, 24f)
    structure.solveForTarget(target)
    structure.debugSVG(target = target)
}

@FlowPreview
fun button(): Flow<Boolean> = flow {
    val sensor = EV3TouchSensor(SensorPort.S2)
    while (true) {
        emit(sensor.isPressed)
        delay(100)
    }
}.debounce(200)
    .conflate()
    .distinctUntilChanged()

suspend fun Flow<Boolean>.waitForPress() {
    filter { it }.first()
}

@FlowPreview
suspend fun getRanges(
    leftShoulder: BaseRegulatedMotor,
    rightShoulder: BaseRegulatedMotor
): Pair<ClosedFloatingPointRange<Float>, ClosedFloatingPointRange<Float>> {
    println("SPIN LEFT!  Move left-shoulder to 45° down-left, right-shoulder to 45° up-left, then click.")
    button().waitForPress()
    val leftMin = leftShoulder.position
    val rightMin = rightShoulder.position
    println("SPIN RIGHT! Move left-shoulder to 45° up-right, right-shoulder to 45° down-right, then click.")
    button().waitForPress()
    val leftMax = leftShoulder.position
    val rightMax = rightShoulder.position

    return Pair(leftMin..leftMax, rightMin..rightMax)
}

