package info.benjaminhill.bot

import kotlinx.coroutines.*
import mu.KotlinLogging
import kotlin.random.Random

internal val logger = KotlinLogging.logger {}

@ExperimentalCoroutinesApi
fun main() = runBlocking(Dispatchers.Default) {
    println("Logger Debug: ${logger.isDebugEnabled}")
    //val motorJaw: RegulatedMotor = EV3LargeRegulatedMotor(MotorPort.C)

    logger.info { "Loading audio clips..." }
    val phrase = Phrases.expandPhrase("costume", Phrases.Triggers.COSTUME_GENERIC).toWav().map { AudioClip(it) }

    // Phrases.expandPhrase("spooky ghost", Phrases.Triggers.COSTUME_GREETING)
    // Phrases.expandPhrase("Which house has the best candy?")
    launch(Dispatchers.IO) {
        for (clip in phrase) {
            clip.play()
        }
    }
    // wait for phrase to start
    while (phrase.first().state == AudioClip.State.PENDING) {
        delay(100)
    }

    logger.info { "Starting random jaw movements (sound should be started)." }
    while (phrase.last().state != AudioClip.State.DONE) {
        val newJawTarget = Random.nextInt(0, 60)
        //logger.info { "Jaw from (sp:${motorJaw.speed}, tc:${motorJaw.tachoCount} mx:${motorJaw.maxSpeed}) to ang:$newJawTarget" }

        // Because of pause after move.  Hope don't have to time it...
        // motorJaw.speed = Random.nextInt(80, 150)
        //motorJaw.rotateTo(newJawTarget, true)
        // Wait for move to finish OR phrase to end
        while (
        //motorJaw.isMoving &&
        //!motorJaw.isStalled &&
            phrase.last().state != AudioClip.State.DONE) {
            delay(100)
        }

    }
    logger.info { "Phrase finished, resetting jaw to 0." }
    //motorJaw.rotateTo(0)
    //motorJaw.flt()

    Unit
}

/** Phrase (list) to wav file (list): []that_is.wav, very.wav, impressive.wav, makeup.wav] */
internal fun List<String>.toWav() = this.filter { it.isNotEmpty() }.map {
    it.toLowerCase().replace(Regex("\\W+"), "_").trim('_') + ".wav"
}