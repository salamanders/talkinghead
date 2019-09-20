package info.benjaminhill.bot

import kotlin.random.Random

object Phrases {
    /** Given a phrase type and choice, inserts wildcards and returns a list for saying */
    fun expandPhrase(phrase: String, phraseClass: Triggers) = when (phraseClass) {
        Triggers.COSTUME_GENERIC -> listOf(owner, adverb, adjective, phrase)
        Triggers.COSTUME_GREETING -> listOf(greeting, phrase)
        else -> listOf(phrase)
    }

    /** All supported phrase, for a UI selector */
    fun getPhrases() = phraseMap

    private fun <E> List<E>.getRandomElement() = this[Random.nextInt(this.size)]

    private val owner: String
        get() = listOf("You have", "That is", "I like your").getRandomElement()

    private val adverb: String
        get() = listOf("very", "extremely", "").getRandomElement()

    private val adjective: String
        get() = listOf("scary", "impressive").getRandomElement()

    private val greeting: String
        get() = listOf("Hello", "Pleased to meet you", "Greetings", "Well met", "All hail").getRandomElement()

    enum class Triggers {
        COSTUME_GREETING,
        COSTUME_GENERIC
    }

    private val phraseMap: Map<String, Set<String>> = mapOf(
        "Environment" to setOf(
            "Ohh, pretty lights.",
            "These are great decorations.",
            "That is very impressive.",
            "Is that home-made?",
            "Did you create that using a dark spell?",
            "I like the skeletons the most.",
            "Too cheery.",
            "That is very impressive.",
            "That is very scary."
        ),
        "Trick or Treat" to setOf(
            "Just drop some candy in my mouth.",
            "Candy skull says ahhhhhhhhhhh.",
            "Trick or treat, smell my feet.  Wait, where are my feet?",
            "Happy Halloween!",
            "Boo.",
            "Mu Hah Hah Hah Ah Ha Hah.",
            "Which house has the best candy?"
        ),
        "Compliment" to setOf(
            "Nice costume kid.",
            "I get it, you all go together!",
            "Are you a real monster?",
            "Hey, aren't you a bit old to be trick or treating?",
            "You scared me!"
        ),
        "Self" to setOf(
            "Do you like my horns?",
            "Has anyone seen the rest of my body?",
            "I'm really getting a-head.",
            "I'm a daemon.",
            "I'm bad to the bone.",
            "Why am I so scared?  Because I've got no guts!  Get it?",
            "For many millennia.",
            "With Magic."
        ),
        Triggers.COSTUME_GENERIC.toString() to setOf(
            "costume",
            "makeup"
        ),
        Triggers.COSTUME_GREETING.toString() to setOf(
            "Spider-man.",
            "Iron-man.",
            "Captain America",
            "brave knight.",
            "spooky ghost.",
            "powerful witch.",
            "Disney character.",
            "Disney Princess.",
            "scary clown.  Clowns are terrifying.",
            "Mal.  I know your mother.",
            "Maleficent. I saw Mal earlier.",
            "Space Ranger.",
            "superhero",
            "transformer",
            "Marvel character.",
            "video game character.",
            "abstract concept.",
            "evil villain."
        ),
        "Specific Costume" to setOf(
            "Pika pika.",
            "I choose you.",
            "LEGOs!  Can I take you apart?",
            "What are you?",
            "Where did you come from?"
        ),
        "Yes" to setOf(
            "I am.",
            "Yes please.",
            "You are correct.",
            "Indeed.",
            "Thank you!"
        ),
        "No" to setOf(
            "I am not.",
            "No.",
            "No grabbing.",
            "No thank you."
        )
    )

}