package eu.rudisch.longpolling

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

//@SpringBootApplication
class LongPollingApplication

fun mains(args: Array<String>) {
	runApplication<LongPollingApplication>(*args)
}
