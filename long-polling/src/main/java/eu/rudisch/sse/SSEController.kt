package eu.rudisch.sse

import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink

import java.time.Duration
import java.time.LocalTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.function.Consumer

@RestController
@RequestMapping("/sse-server")
class SSEController {
	@PostMapping("post")
	fun post() {
		alerts.add("SSE - " + LocalTime.now())
		hashMap.put("SSE - " + LocalTime.now(), "test")
	}

	@GetMapping("/stream-sse")
	fun streamEvents(): Flux<ServerSentEvent<String>> {
		return Flux.interval(Duration.ofSeconds(1))
				.map { sequence ->
					ServerSentEvent.builder<String>()
							.id(sequence.toString())
							.event("periodic-event")
							.data("SSE - " + LocalTime.now()
									.toString())
							.build()
				}
	}

	@GetMapping(path = ["/stream-flux"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
	fun streamFlux(): Flux<String> {
		return Flux.create { sink: FluxSink<String> ->
			/* get all the latest article from a server and
	   emit them one by one to downstream. */
			val articles = alerts
			hashMap.keys.forEach(Consumer<String> { sink.next(it) })
		}
//		return Flux.fromIterable(alerts)
//		return Flux.interval(Duration.ofSeconds(1))
//				.map { _ ->
//					"Flux - " + LocalTime.now()
//							.toString()
//				}
	}

	companion object {
		lateinit var hashMap: ConcurrentHashMap<String, String>
		lateinit var alerts: CopyOnWriteArrayList<String>
	}

	init {
		hashMap = ConcurrentHashMap()
		alerts = CopyOnWriteArrayList()
	}
}