package info.benjaminhill.bot.device


import java.io.File

/**
 * http://docs.ev3dev.org/projects/lego-linux-drivers/en/ev3dev-stretch/sensors.html#automatic-detection
 *
 * value<N> / 10.0 ^ decimals
 * Look for driver_name
 * BrickPi3 needs to set the port to ev3-uart mode then write the driver name to set_device
 */
abstract class Sensor(dir: File) : Device(dir) {
    override val port = dirToPort<Port>(deviceDir)

    enum class Port {
        S1,
        S2,
        S3,
        S4,
    }
}