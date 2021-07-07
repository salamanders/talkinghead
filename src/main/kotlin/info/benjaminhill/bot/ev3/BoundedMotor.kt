package info.benjaminhill.bot.ev3

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.time.ExperimentalTime

class BoundedMotor(port: Port) : TachoMotor(port) {
    private lateinit var tachoBounds: ClosedRange<Int>

    @ExperimentalCoroutinesApi
    @ExperimentalTime
    suspend fun calibrate() {
        println("Motor $port set LOWER bounds: (d=decrease, q=quit, i=increase):")
        clicks().collect {
            when (it) {
                'd' -> rotate(-0.25)
                'i' -> rotate(0.25)
            }
        }
        val tachoMin = updates().first().position

        println("Motor $port set UPPER bounds: (d=decrease, q=quit, i=increase):")
        clicks().collect {
            when (it) {
                'd' -> rotate(-0.25)
                'i' -> rotate(0.25)
            }
        }
        val tachoMax = updates().first().position
        tachoBounds = tachoMin..tachoMax
    }
}

fun clicks(quitChar: Char = 'q'): Flow<Char> = flow {
    Scanner(System.`in`).use { input ->
        do {
            val ch = input.next().single()
            emit(ch)
        } while (ch != quitChar)
    }
}.cancellable()