package info.benjaminhill.bot.ev3

import info.benjaminhill.bot.device.Sensor
import java.io.File


open class UltrasonicSensor(port: Port) : Sensor(portToDir(port, ROOT_DIR)) {
    companion object {
        val ROOT_DIR = File("TODO")
    }
}
