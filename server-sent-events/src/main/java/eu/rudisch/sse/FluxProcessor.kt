package eu.rudisch.sse

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.http.codec.ServerSentEvent
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.LocalTime
import java.time.temporal.TemporalUnit
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

@Service
class FluxProcessor<T> {

	companion object {
		private val logger = LoggerFactory.getLogger(FluxProcessor::class.java)
	}

	private val listeners: CopyOnWriteArrayList<Consumer<ServerSentEvent<T>>> = CopyOnWriteArrayList()

	private val history: ConcurrentHashMap<String, ServerSentEvent<T>> = ConcurrentHashMap()

	fun process(event: String, data: T) {
		logger.info("process: $data")
		listeners.forEach {
			val sse = ServerSentEvent.builder<T>()
					.id("dummy-Id")
					.event(event)
					.data(data)
					.retry(Duration.ofMillis(3000))
					.build()
			it.accept(sse)
		}
	}

	fun emit(): Flux<ServerSentEvent<T>> {
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

	private fun register(listener: Consumer<ServerSentEvent<T>>) {
		logger.info("Added a listener, for a total of $listeners.size listener(s)")
		listeners.add(listener)
	}
}