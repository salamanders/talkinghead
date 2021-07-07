package info.benjaminhill.bot.ev3

import ev3dev.actuators.lego.motors.BaseRegulatedMotor
import ev3dev.sensors.ev3.EV3TouchSensor
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import lejos.hardware.port.SensorPort

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
