package eu.rudisch.sse

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

@Service
class MessageProcessor {

	companion object {
		private val logger = LoggerFactory.getLogger(MessageProcessor::class.java)
		private lateinit var listeners: CopyOnWriteArrayList<Consumer<String>>
	}

	init {
		listeners = CopyOnWriteArrayList()
	}

	fun register(listener: Consumer<String>) {
		logger.info("Added a listener, for a total of $listeners.size listener(s)")
		listeners.add(listener)
	}

	fun process(message: String) {
		logger.info("process: $message")
		listeners.forEach {
			it.accept(message)
		}
	}
}