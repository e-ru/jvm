package eu.rudisch.authorization.admin.config

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CorsFilter : Filter {
	override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain?) {
		if (response is HttpServletResponse) {
			response.setHeader("Access-Control-Allow-Origin", "*")
			response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE")
			response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type")
			response.setHeader("Access-Control-Max-Age", "3600")
			if (HttpMethod.OPTIONS.name.equals((request as HttpServletRequest).method, ignoreCase = true)) {
				response.status = HttpServletResponse.SC_OK
			} else {
				chain!!.doFilter(request, response)
			}
		}
	}
}