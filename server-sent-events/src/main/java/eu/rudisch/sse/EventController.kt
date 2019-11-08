package eu.rudisch.sse

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import java.net.URI
import java.time.LocalTime

@RestController
@RequestMapping("/events")
class EventController(
		@Qualifier("sseProcessor")
		private val sseFluxProcessor: FluxProcessor<String>) {

	companion object {
		private val logger = LoggerFactory.getLogger(EventController::class.java)
	}

	@PostMapping()
	fun send(@RequestBody data: String) : ResponseEntity<String>{
		logger.info("Received '{}'", data)
		sseFluxProcessor.process(
				"sse-event",
				"SSE - data: $data ${LocalTime.now()}"
		)
		return ResponseEntity.created(URI("https://localhost:9393/events")).build<String>()
	}

	@GetMapping()
	fun receiveSSE(): Flux<ServerSentEvent<String>> {
		return sseFluxProcessor.emit()
	}
}