package eu.rudisch.sse

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

@Service
class Processor<T> {

	companion object {
		private val logger = LoggerFactory.getLogger(Processor::class.java)
	}
	private val listeners: CopyOnWriteArrayList<Consumer<T>> = CopyOnWriteArrayList()

	fun register(listener: Consumer<T>) {
		logger.info("Added a listener, for a total of $listeners.size listener(s)")
		listeners.add(listener)
	}

	fun process(t: T) {
		logger.info("process: $t")
		listeners.forEach {
			it.accept(t)
		}
	}
}