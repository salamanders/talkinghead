package info.benjaminhill.bot.device

import info.benjaminhill.utils.changesToFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import java.io.File
import java.util.*
import kotlin.time.ExperimentalTime

/**
 * http://docs.ev3dev.org/projects/lego-linux-drivers/en/ev3dev-stretch/sensors.html#automatic-detection
 *
 * value<N> / 10.0 ^ decimals
 * Look for driver_name
 */
abstract class Motor(dir: File) : Device(dir) {

    private val speedFile = File(deviceDir, "speed")
    private val stateFile = File(deviceDir, "state")
    protected val commandFile = File(deviceDir, "command")
    protected val positionSpFile = File(deviceDir, "position_sp")

    override val port = dirToPort<Port>(deviceDir)

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    protected fun statusUpdates(): Flow<EnumSet<State>> = stateFile
        .changesToFlow()
        .onStart { println("Status flow for motor $port onStart") }
        .onCompletion { println("Status flow for motor $port onCompletion") }
        .map { State.of(it) }

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    protected fun speedUpdates(): Flow<Int> = speedFile
        .changesToFlow()
        .onStart { println("Speed flow for motor $port onStart") }
        .onCompletion { println("Speed flow for motor $port onCompletion") }
        .map { it.trim().toInt() }

    enum class Port {
        MA,
        MB,
        MC,
        MD,
    }

    enum class State {
        RUNNING, // Power is being sent to the motor.
        RAMPING, // The motor is ramping up or down and has not yet reached a constant output level.
        HOLDING, // The motor is not turning, but rather attempting to hold a fixed position.
        OVERLOADED, // The motor is turning as fast as possible, but cannot reach its speed_sp.
        STALLED; // The motor is trying to run but is not turning at all.

        companion object {
            fun of(state: String): EnumSet<State> {
                try {
                    return EnumSet.copyOf(state.split(" ")
                        .map { valueOf(it.uppercase(Locale.getDefault())) })
                } catch (e: IllegalArgumentException) {
                    println("Failed to parse: '$state'")
                    throw(e)
                }
            }
        }
    }

}