package eu.rudisch.sse

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerSentEvent

@Configuration
class ProcessorConfiguration {

	@Bean("sseProcessor")
	fun sseProcessor(): FluxProcessor<ServerSentEvent<String>> {
		return FluxProcessor()
	}
}