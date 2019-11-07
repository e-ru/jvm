package eu.rudisch.sse

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [RedisReactiveAutoConfiguration::class])
class ServerSSEApplication

fun main(args: Array<String>) {
	runApplication<ServerSSEApplication>(*args)
}
