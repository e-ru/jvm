package eu.rudisch.authorization.admin.config

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore

private const val RESOURCE_ID = "oauth2-control-resource"
private const val ADMIN_CREATE_SCOPE = "#oauth2.hasScope('create_oauth')"
private const val ADMIN_READ_SCOPE = "#oauth2.hasScope('read_oauth')"
private const val ADMIN_UPDATE_SCOPE = "#oauth2.hasScope('update_oauth')"
private const val ADMIN_DELETE_SCOPE = "#oauth2.hasScope('delete_oauth')"

private const val ADMIN_PATTERN = "/admin/**"

@Configuration
@EnableResourceServer
class AuthorizationAdminConfig : ResourceServerConfigurerAdapter() {

	@Autowired
	private val customAccessTokenConverter: CustomAccessTokenConverter? = null

	@Autowired
	private val restTokenKeyFetcher: RestTokenKeyFetcher? = null

	override fun configure(resources: ResourceServerSecurityConfigurer) {
		resources.resourceId(RESOURCE_ID).tokenServices(tokenServices())
	}

	@Throws(Exception::class)
	override fun configure(http: HttpSecurity) {
		http.requestMatchers()
				.and().authorizeRequests()
				.antMatchers(HttpMethod.POST, ADMIN_PATTERN).access(ADMIN_CREATE_SCOPE)
				.antMatchers(HttpMethod.GET, ADMIN_PATTERN).access(ADMIN_READ_SCOPE)
				.antMatchers(HttpMethod.PUT, ADMIN_PATTERN).access(ADMIN_UPDATE_SCOPE)
				.antMatchers(HttpMethod.DELETE, ADMIN_PATTERN).access(ADMIN_DELETE_SCOPE)
	}

	@Bean
	fun accessTokenConverter(): JwtAccessTokenConverter {
		val converter = JwtAccessTokenConverter()
		converter.accessTokenConverter = customAccessTokenConverter

		val tokenKey = restTokenKeyFetcher!!.getTokenKey()

		converter.setVerifierKey(tokenKey)
		return converter
	}

	@Bean
	fun tokenStore(): TokenStore {
		return JwtTokenStore(accessTokenConverter())
	}

	@Bean
	@Primary
	fun tokenServices(): DefaultTokenServices {
		val defaultTokenServices = DefaultTokenServices()
		defaultTokenServices.setTokenStore(tokenStore())
		return defaultTokenServices
	}
}