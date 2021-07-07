package info.benjaminhill.bot.ev3

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.DocumentChange
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.FirestoreOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import mu.KotlinLogging
import java.io.File


private val logger = KotlinLogging.logger {}

private const val ADMIN_JSON = "ev3-grabber-firebase-adminsdk-ez37o-2b1b001dcd.json"

internal val firestoreDb: Firestore by lazy {
    logger.info { "Connecting to Firestore..." }

    val inputStr = ClassLoader.getSystemClassLoader().getResourceAsStream(ADMIN_JSON)
        ?: File("./resources/$ADMIN_JSON").let { if (it.canRead()) it.inputStream() else null }
        ?: File("/resources/$ADMIN_JSON").let { if (it.canRead()) it.inputStream() else null }
        ?: error("Unable to open resource: '$ADMIN_JSON'")

    FirestoreOptions.newBuilder().setCredentials(
        GoogleCredentials.fromStream(inputStr)
    ).build().service!!.also {
        logger.debug { "Connected, generated service." }
    }
}

internal fun CollectionReference.clearAll() {
    logger.info { "${this.path} looking for documents to clear" }
    if (this.get().get().isEmpty) {
        logger.info { " already empty." }
    } else {
        firestoreDb.batch().let { writeBatch ->
            var numToDelete = 0
            listDocuments().forEach {
                writeBatch.delete(it)
                numToDelete++
            }
            if (numToDelete > 0) {
                logger.info { " Clearing $numToDelete documents..." }
                writeBatch.commit().get()
                logger.debug { " done." }
            } else {
                logger.info { "${this.path} no docs to clear (unexpected)" }
            }
        }
    }
}

@ExperimentalCoroutinesApi
fun CollectionReference.toFlow(): Flow<DocumentChange> = callbackFlow {
    val snapshotListener = this@toFlow.addSnapshotListener { snapshots, e ->
        if (e != null) {
            logger.warn { "${this@toFlow.path} listen:error:$e" }
            cancel(CancellationException("Collection Listener Error in ${this@toFlow.path}", e))
            return@addSnapshotListener
        }
        for (dc in snapshots!!.documentChanges.filterNotNull()) {
            trySendBlocking(dc).onFailure { logger.warn { "Failed to send collection change." } }
        }
    }
    awaitClose {
        logger.info { "Closing collection's flow, removing listener." }
        snapshotListener.remove()
        channel.close()
    }
}