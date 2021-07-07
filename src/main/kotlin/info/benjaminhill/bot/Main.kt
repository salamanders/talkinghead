package info.benjaminhill.bot


import com.google.cloud.firestore.DocumentChange
import ev3dev.actuators.lego.motors.BaseRegulatedMotor
import ev3dev.actuators.lego.motors.EV3LargeRegulatedMotor
import ev3dev.actuators.lego.motors.EV3MediumRegulatedMotor
import info.benjaminhill.bot.ev3.firestoreDb
import info.benjaminhill.bot.ev3.toFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import lejos.hardware.port.MotorPort
import lejos.hardware.port.Port
import mu.KotlinLogging
import org.slf4j.impl.SimpleLogger
import org.slf4j.impl.StaticLoggerBinder


internal val logger = KotlinLogging.logger {}

enum class Ports {
    A, B, C, D,
    S1, S2, S3, S4
}

val Port.eName: Ports
    get() = Ports.valueOf(this.name)

@ExperimentalCoroutinesApi
fun main() {
    System.setProperty(SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "INFO")

// 1. Determine what logging framework SLF4J is bound to:
    val binder: StaticLoggerBinder = StaticLoggerBinder.getSingleton()
    println(binder.loggerFactoryClassStr)

    println("APP:START")
    println("Debug Enabled: ${logger.isDebugEnabled}")


    println(MotorPort.A.eName)
    val motors: Map<Ports, BaseRegulatedMotor> = try {
        mapOf(
            MotorPort.A.eName to EV3MediumRegulatedMotor(MotorPort.A),
            MotorPort.B.eName to EV3LargeRegulatedMotor(MotorPort.B),
            MotorPort.C.eName to EV3LargeRegulatedMotor(MotorPort.C),
            MotorPort.D.eName to EV3MediumRegulatedMotor(MotorPort.D)
        )
    } catch (e: NoSuchElementException) {
        println("Not running on an EV3 brick?")
        mapOf()
    }

    runBlocking(Dispatchers.IO) {
        firestoreDb.collection("motors").toFlow().collect { dc: DocumentChange ->
            val docSnapshot = dc.document
            val port = Ports.valueOf(docSnapshot.getString("name")!!)
            val action = docSnapshot.getString("action") ?: error("Missing 'action'")
            val motor = motors[port] ?: error("Unable to find motor:$port")
            val newValue = docSnapshot.get("value")
            motorAction(motor, action, newValue)
        }
    }
}

fun motorAction(motor: BaseRegulatedMotor, action: String, newValue: Any?) {

}
