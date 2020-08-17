package info.benjaminhill.bot.ev3

import info.benjaminhill.bot.device.Motor
import info.benjaminhill.utils.LogInfrequently
import info.benjaminhill.utils.changesToFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.io.File
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.seconds


/**
 * http://docs.ev3dev.org/projects/lego-linux-drivers/en/ev3dev-stretch/motors.html
 * Commands: run-forever run-to-abs-pos run-to-rel-pos run-timed run-direct stop reset
 * @param port Directory of the TachoMotor (dir containing the "address" file)
 */
open class TachoMotor(override val port: Port) : Motor(portToDir(port, ROOT_DIR)) {

    private val positionFile = File(deviceDir, "position")
    private val tachosPerRotation = File(deviceDir, "count_per_rot").readText().trim().toInt()
    @ExperimentalTime
    private val linf = LogInfrequently(delay = 0.5.seconds)

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    private fun positionUpdates(): Flow<Int> = positionFile
        .changesToFlow()
        .onStart { println("Position flow for motor $port onStart") }
        .onCompletion { println("Position flow for motor $port onCompletion") }
        .map { it.trim().toInt() }

    /**
     * @return State, Speed, Position
     */
    @ExperimentalCoroutinesApi
    @ExperimentalTime
    fun updates(): Flow<Update> = statusUpdates()
        .combine(speedUpdates()) { status, speed -> Pair(status, speed) }
        .combine(positionUpdates()) { (status, speed), position ->
            Update(status, speed, position)
        }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    suspend fun awaitStopped() = updates().filter { update ->
        val (status, speed, position) = update
        (!status.contains(State.RUNNING) || speed == 0).also {
            linf.hit { "Filtering out while anything moving: '$update'=$it" }
        }
    }.first()

    @ExperimentalTime
    fun rotate(rotations: Double) {
        positionSpFile.writeText((tachosPerRotation * rotations).toInt().toString())
        commandFile.writeText(Command.RUN_TO_REL.str)
    }

    enum class Command(val str: String) {
        RUN_TO_REL("run-to-rel-pos"),
    }

    companion object {
        val ROOT_DIR = File("/sys/class/tacho-motor")

        data class Update(
            val state: EnumSet<State>,
            val speed: Int,
            val position: Int,
        )
    }
}