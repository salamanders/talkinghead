package info.benjaminhill.bot


import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import javax.sound.sampled.DataLine
import javax.sound.sampled.LineEvent
import kotlin.coroutines.resume

// TODO: https://stackoverflow.com/questions/11017283/java-program-to-create-a-png-waveform-for-an-audio-file

/** Single-shot suspending WAV audio player */
class AudioClip(private val audioFilePath: String) {
    enum class State { PENDING, PLAYING, DONE }

    private val audioFile = File(audioFilePath).also {
        require(it.canRead()) { "Missing audio file $audioFilePath" }
    }
    private val audioInputStream = AudioSystem.getAudioInputStream(audioFile)!!
    private val format = audioInputStream.format!!
    private val info = DataLine.Info(Clip::class.java, format)
    private val clip = (AudioSystem.getLine(info) as Clip).also {
        it.open(audioInputStream)
    }
    var state: State = State.PENDING
        private set

    fun getMs(): Long = (audioInputStream.frameLength * 1000.0 / audioInputStream.format.frameRate).toLong()

    /**
     * Plays an audio clip, returns when done.
     */
    @ExperimentalCoroutinesApi
    suspend fun play() = suspendCancellableCoroutine<Unit> { cont ->
        clip.apply {
            addLineListener { event ->
                when (event.type) {
                    LineEvent.Type.START -> {
                        state = State.PLAYING
                        logger.debug { "Started clip: ${File(audioFilePath).name}" }
                    }
                    LineEvent.Type.STOP -> {
                        state = State.DONE
                        cont.resume(Unit)
                        logger.debug { "Ended clip: ${File(audioFilePath).name}, closing." }
                        close()
                    }
                }
            }
            start()
        }
    }
}

