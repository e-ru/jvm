package eu.rudisch.authorization.admin.config

import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter

class AuthorizationAdminConfig : ResourceServerConfigurerAdapter() {
	companion object {
		val info = "This is info"
		fun getMoreInfo(): String {
			return "This is more fun"
		}
	}
}