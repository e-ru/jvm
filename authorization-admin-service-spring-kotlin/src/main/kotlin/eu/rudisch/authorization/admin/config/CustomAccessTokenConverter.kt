package eu.rudisch.authorization.admin.config

import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter
import org.springframework.stereotype.Component

@Component
class CustomAccessTokenConverter: DefaultAccessTokenConverter() {
	override fun extractAuthentication(claims: Map<String, *>): OAuth2Authentication {
		val authentication = super.extractAuthentication(claims)
		authentication.details = claims
		return authentication
	}
}