package eu.rudisch.sse

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*
import java.util.function.Consumer

@RestController
@RequestMapping("/events")
class EventController(
		@Qualifier("stringProcessor")
		private val stringProcessor: Processor<String>,
		@Qualifier("sseProcessor")
		private val sseProcessor: Processor<ServerSentEvent<String>>) {

	companion object {
		private val logger = LoggerFactory.getLogger(EventController::class.java)
	}

	@PostMapping()
	@ResponseStatus(value = HttpStatus.CREATED)
	fun send(@RequestBody event: String) {
		logger.info("Received '{}'", event)
		stringProcessor.process(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()) + " " + event)
		sseProcessor.process(
				ServerSentEvent.builder<String>()
						.id(event)
						.event("periodic-event")
						.data("SSE - " + LocalTime.now().toString())
						.build()
		)
	}

	@GetMapping()
	fun receiveSSE(): Flux<ServerSentEvent<String>> {
		return Flux.create { sink ->
			sink?.let {
				sseProcessor.register(Consumer { sink.next(it) })
			}
		}
	}

	@GetMapping(path = ["/flux"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
	fun receiveFlux(): Flux<String> {
		// Some FluxSink documentation and code samples:
		// - https://projectreactor.io/docs/core/release/reference/#producing.create
		// - https://www.baeldung.com/reactor-core
		// - https://www.e4developer.com/2018/04/14/webflux-and-servicing-client-requests-how-does-it-work/

		return Flux.create { sink ->
			sink?.let {
				stringProcessor.register(Consumer { sink.next(it) })
			}
		}
	}

	@GetMapping(path = ["/timestamps"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
	fun timestamps(): Flux<String> {
		return Flux.interval(Duration.ofSeconds(1))
				.map { _ -> LocalTime.now().toString() }
	}
}