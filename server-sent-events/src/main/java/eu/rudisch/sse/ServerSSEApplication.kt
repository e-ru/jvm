package eu.rudisch.sse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [RedisReactiveAutoConfiguration::class])
class ServerSSEApplication {
//	@Bean
//	fun sseServerSpringSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
//		http.authorizeExchange()
//				.anyExchange()
//				.permitAll()
//		return http.build()
//	}
}

fun main(args: Array<String>) {
	runApplication<ServerSSEApplication>(*args)
}

//@Bean
//fun sseServerSpringSecurityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
//	http.authorizeExchange()
//			.anyExchange()
//			.permitAll()
//	return http.build()
//}

//@Bean
//fun servletWebServerFactory(): ServletWebServerFactory {
//	return TomcatServletWebServerFactory()
//}
