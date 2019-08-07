package eu.rudisch.authorization.admin.config

import eu.rudisch.authorization.admin.web.controller.resource.TokenKey
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class RestTokenKeyFetcher {
	@Value("\${custom.oauth.server}")
	private val oauthServer: String? = null

	internal fun getTokenKey(): String {
		val restTemplate = RestTemplate()
		val tokenKey = restTemplate.getForObject<TokenKey>("$oauthServer/oauth/token_key", TokenKey::class.java)
		return tokenKey?.value ?: ""
	}
}