package eu.rudisch.sse

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.time.LocalTime

@RestController
@RequestMapping("/events")
class EventController(
		@Qualifier("sseProcessor")
		private val sseFluxProcessor: FluxProcessor<ServerSentEvent<String>>) {

	companion object {
		private val logger = LoggerFactory.getLogger(EventController::class.java)
	}

	@PostMapping()
	@ResponseStatus(value = HttpStatus.CREATED)
	fun send(@RequestBody event: String) {
		logger.info("Received '{}'", event)
		sseFluxProcessor.process(
				ServerSentEvent.builder<String>()
						.id(event)
						.event("periodic-event")
						.data("SSE - " + LocalTime.now().toString())
						.build()
		)
	}

	@GetMapping()
	fun receiveSSE(): Flux<ServerSentEvent<String>> {
		return sseFluxProcessor.emit()
	}
}