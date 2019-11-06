package eu.rudisch.sse

import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.util.*
import java.util.function.Consumer

@RestController
class MessageController(private val messageProcessor: MessageProcessor) {
	companion object {
		private val logger = LoggerFactory.getLogger(MessageProcessor::class.java)
	}

	@PostMapping("/send")
	fun send(@RequestBody message: String): String {
		logger.info("Received '{}'", message)
		messageProcessor.process(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()) + " " + message)
		return "Done"
	}

	@GetMapping(path = ["/receive"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
	fun receive(): Flux<String> {
		// Some FluxSink documentation and code samples:
		// - https://projectreactor.io/docs/core/release/reference/#producing.create
		// - https://www.baeldung.com/reactor-core
		// - https://www.e4developer.com/2018/04/14/webflux-and-servicing-client-requests-how-does-it-work/

		return Flux.create { sink ->
			sink?.let {
				messageProcessor.register(Consumer { sink.next(it) })
			}
		}
	}

	@GetMapping(path = ["/timestamps"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
	fun timestamps(): Flux<String> {
		return Flux.interval(Duration.ofSeconds(1))
				.map { sequence -> LocalTime.now().toString() }
	}
}