package eu.rudisch.sse

import org.slf4j.LoggerFactory
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

@Service
class FluxProcessor<T> {

	companion object {
		private val logger = LoggerFactory.getLogger(FluxProcessor::class.java)
	}

	private val listeners: CopyOnWriteArrayList<Consumer<ServerSentEvent<T>>> = CopyOnWriteArrayList()

	private val history: ConcurrentHashMap<Int, ServerSentEvent<T>> = ConcurrentHashMap()

	// TODO: synchronize this function
	// TODO: usecase process
	fun process(event: String, data: T) {
		logger.info("process: $data")
		val sse = ServerSentEvent.builder<T>()
				.id("dummy-Id")
				.event(event)
				.data(data)
				.retry(Duration.ofMillis(3000))
				.build()
		listeners.forEach { l ->
			l.accept(sse)
		}
		history[history.size + 1] = sse
	}

	//TODO: usecase emit
	fun emit(): Flux<ServerSentEvent<T>> {
		// Some FluxSink documentation and code samples:
		// - https://projectreactor.io/docs/core/release/reference/#producing.create
		// - https://www.baeldung.com/reactor-core
		// - https://www.e4developer.com/2018/04/14/webflux-and-servicing-client-requests-how-does-it-work/
		return Flux.create { sink ->
			sink?.let {
				logger.info("Added a listener, for a total of $listeners.size listener(s)")
				val listener = Consumer<ServerSentEvent<T>> { sink.next(it) }
				listeners.add(listener)
				history.values.forEach { s ->
					listener.accept(s)
				}
			}
		}
	}

	private fun register(listener: Consumer<ServerSentEvent<T>>) {
		logger.info("Added a listener, for a total of $listeners.size listener(s)")
		listeners.add(listener)
	}
}