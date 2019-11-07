package eu.rudisch.sse

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerSentEvent

@Configuration
class ProcessorConfiguration {

	@Bean("stringProcessor")
	fun stringProcessor() : Processor<String>{
		return Processor()
	}

	@Bean("sseProcessor")
	fun sseProcessor() : Processor<ServerSentEvent<String>>{
		return Processor()
	}
}