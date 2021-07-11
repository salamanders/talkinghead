package info.benjaminhill.bot.device

import java.io.File

abstract class Device(
    protected val deviceDir: File
) {
    init {
        require(deviceDir.exists()) { "Unable to read device directory '${deviceDir.absolutePath}'" }
    }

    val portNum = deviceDir.parent.last().code // TODO: This may not equal the other port number
    abstract val port: Enum<*>

    companion object {
        const val ADDRESS_FILE = "address"

        inline fun <reified T : Enum<T>> portToDir(port: T, rootDir: File) = rootDir
            .listFiles()!!
            .filterNotNull()
            .first { dirPortN ->
                val portName = File(dirPortN, ADDRESS_FILE).readText().trim().split(":").last()
                enumValueOf<T>(portName) == port
            }

        inline fun <reified T : Enum<T>> dirToPort(deviceDir: File): T = File(deviceDir, ADDRESS_FILE)
            .readText().trim().split(":").last()
            .let { enumValueOf(it) }
    }
}