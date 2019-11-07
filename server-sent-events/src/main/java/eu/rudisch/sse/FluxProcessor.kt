package eu.rudisch.sse

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

@Service
class FluxProcessor<T> {

	companion object {
		private val logger = LoggerFactory.getLogger(FluxProcessor::class.java)
	}

	private val listeners: CopyOnWriteArrayList<Consumer<T>> = CopyOnWriteArrayList()

	fun process(t: T) {
		logger.info("process: $t")
		listeners.forEach {
			it.accept(t)
		}
	}

	fun emit(): Flux<T> {
		// Some FluxSink documentation and code samples:
		// - https://projectreactor.io/docs/core/release/reference/#producing.create
		// - https://www.baeldung.com/reactor-core
		// - https://www.e4developer.com/2018/04/14/webflux-and-servicing-client-requests-how-does-it-work/
		return Flux.create { sink ->
			sink?.let {
				register(Consumer { sink.next(it) })
			}
		}
	}

	private fun register(listener: Consumer<T>) {
		logger.info("Added a listener, for a total of $listeners.size listener(s)")
		listeners.add(listener)
	}
}