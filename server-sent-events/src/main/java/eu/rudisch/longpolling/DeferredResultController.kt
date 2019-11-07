package eu.rudisch.longpolling

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.async.DeferredResult
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ForkJoinPool

@RestController
class DeferredResultController {

	@PostMapping("post")
	fun post() {
		// trigger.triggered = !trigger.triggered
		suspendedRequests["1"]?.setResult(ResponseEntity.ok("ok not blocking\n"))
	}

	@GetMapping("/process-blocking")
	fun handleReqSync(model: Model): ResponseEntity<*> {
		while (!trigger.triggered) {
			Thread.sleep(1000)
		}
		return ResponseEntity.ok("ok\n")
	}

	@GetMapping("/async-deferredresult")
	fun handleReqDefResult(model: Model): DeferredResult<ResponseEntity<*>> {
		LOG.info("Received async-deferredresult request")
		val output = DeferredResult<ResponseEntity<*>>()
		suspendedRequests["1"] = output
		output.onCompletion {
			LOG.info("complete")
		}
//		ForkJoinPool.commonPool().submit {
//			LOG.info("Processing in separate thread")
//			try {
//				while (!trigger.triggered) {
//					Thread.sleep(1000)
//				}
//			} catch (e: InterruptedException) {
//			}
//
//			output.setResult(ResponseEntity.ok("ok not blocking\n"))
//		}
		LOG.info("servlet thread freed")
		return output
	}

	@GetMapping("/timeout")
	fun handleReqWithTimeouts(model: Model): DeferredResult<ResponseEntity<*>> {
		LOG.info("Received async request with a configured timeout")
		val deferredResult = DeferredResult<ResponseEntity<*>>(500L)
		deferredResult.onTimeout {
			deferredResult.setErrorResult(
					ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timeout occurred.\n"))
		}
		ForkJoinPool.commonPool().submit {
			LOG.info("Processing in separate thread")
			try {
				Thread.sleep(600L)
				deferredResult.setResult(ResponseEntity.ok("ok"))
			} catch (e: InterruptedException) {
				LOG.info("Request processing interrupted")
				deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("INTERNAL_SERVER_ERROR occurred.\n"))
			}

		}
		LOG.info("servlet thread freed")
		return deferredResult
	}

	@GetMapping("/failed-request")
	fun handleAsyncFailedRequest(model: Model): DeferredResult<ResponseEntity<*>> {
		val deferredResult = DeferredResult<ResponseEntity<*>>()
		ForkJoinPool.commonPool().submit {
			try {
				// Exception occurred in processing
				throw Exception()
			} catch (e: Exception) {
				LOG.info("Request processing failed")
				deferredResult.setErrorResult(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("INTERNAL_SERVER_ERROR occurred.\n"))
			}
		}
		return deferredResult
	}

	companion object {
		private val LOG = LoggerFactory.getLogger(DeferredResultController::class.java)
		lateinit var trigger: Trigger
		lateinit var suspendedRequests: ConcurrentHashMap<String, DeferredResult<ResponseEntity<*>>>
	}

	init {
		trigger = Trigger()
		suspendedRequests = ConcurrentHashMap()
	}
}

data class Trigger(var triggered: Boolean = false)