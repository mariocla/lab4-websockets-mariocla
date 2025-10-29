@file:Suppress("NoWildcardImports")

package websockets

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.websocket.ClientEndpoint
import jakarta.websocket.ContainerProvider
import jakarta.websocket.OnMessage
import jakarta.websocket.Session
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import java.net.URI
import java.util.concurrent.CountDownLatch

private val logger = KotlinLogging.logger {}

@SpringBootTest(webEnvironment = RANDOM_PORT)
class ElizaServerTest {
    @LocalServerPort
    private var port: Int = 0

    @Test
    fun onOpen() {
        logger.info { "This is the test worker" }
        val latch = CountDownLatch(3)
        val list = mutableListOf<String>()

        val client = SimpleClient(list, latch)
        client.connect("ws://localhost:$port/eliza")
        latch.await()
        assertEquals(3, list.size)
        assertEquals("The doctor is in.", list[0])
    }

    @Test
    fun onChat() {
        logger.info { "Test thread" }
        val latch = CountDownLatch(4)
        val list = mutableListOf<String>()

        val client = ComplexClient(list, latch)
        client.connect("ws://localhost:$port/eliza")
        latch.await()
        val size = list.size
        /* 1️ EXPLAIN WHY size = list.size IS NECESSARY
        We store the current value of the list size in a variable to freeze its state.
        Since WebSocket communication is asynchronous, list.size could change while assertions are running.
        Keeping the size fixed avoids depending on potential later state changes.
         */

        // 2️ REPLACE BY assertXXX expression that checks an interval; assertEquals must not be used;
        assertTrue(size in 3..6, "Expected between 3 and 6 messages, got $size")

        /* 3️ EXPLAIN WHY assertEquals CANNOT BE USED AND WHY WE SHOULD CHECK THE INTERVAL
        assertEquals cannot be used here because the exact number of messages is unpredictable.
        The server might send additional separators (“---”) or timing differences may occur.
        Checking an interval makes the test more robust against these variations.
         */

        // 4 COMPLETE assertEquals(XXX, list[XXX]) // Check that there is a “DOCTOR”-style response related to emotions or beliefs.
        assertTrue(
            list.any {
                it.contains("feel", ignoreCase = true) ||
                    it.contains("believe", ignoreCase = true) ||
                    it.contains("enjoy", ignoreCase = true)
            },
            "Expected ELIZA-style response (about feelings or beliefs) but got: $list",
        )
    }
}

@ClientEndpoint
class SimpleClient(
    private val list: MutableList<String>,
    private val latch: CountDownLatch,
) {
    @OnMessage
    fun onMessage(message: String) {
        logger.info { "Client received: $message" }
        list.add(message)
        latch.countDown()
    }
}

@ClientEndpoint
class ComplexClient(
    private val list: MutableList<String>,
    private val latch: CountDownLatch,
) {
    @OnMessage
    fun onMessage(
        message: String,
        session: Session,
    ) {
        logger.info { "Client received: $message" }
        list.add(message)
        latch.countDown()
        if (message.contains("The doctor is in.")) {
            session.basicRemote.sendText("I am feeling sad")
        }
    }
}

fun Any.connect(uri: String) {
    ContainerProvider.getWebSocketContainer().connectToServer(this, URI(uri))
}
