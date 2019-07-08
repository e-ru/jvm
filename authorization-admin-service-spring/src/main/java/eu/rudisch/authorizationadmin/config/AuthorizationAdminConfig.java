package eu.rudisch.authorizationadmin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableResourceServer
public class AuthorizationAdminConfig extends ResourceServerConfigurerAdapter {

	// hasRole: role of the user profile in database
	// hasAuthority: permission of user profile in database
	// #oauth2.hasScope: granted scope during access token creation

	private static final String RESOURCE_ID = "oauth2-control-resource";
	private static final String ADMIN_CREATE_SCOPE = "#oauth2.hasScope('create_oauth')";
	private static final String ADMIN_READ_SCOPE = "#oauth2.hasScope('read_oauth')";
	private static final String ADMIN_UPDATE_SCOPE = "#oauth2.hasScope('update_oauth')";
	private static final String ADMIN_DELETE_SCOPE = "#oauth2.hasScope('delete_oauth')";

	private static final String ADMIN_PATTERN = "/admin/**";

	@Autowired
	private CustomAccessTokenConverter customAccessTokenConverter;

	@Autowired
	private RestTokenKeyFetcher restTokenKeyFetcher;

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(RESOURCE_ID).tokenServices(tokenServices());
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.requestMatchers()
				.and().authorizeRequests()
				.antMatchers(HttpMethod.POST, ADMIN_PATTERN).access(ADMIN_CREATE_SCOPE)
				.antMatchers(HttpMethod.GET, ADMIN_PATTERN).access(ADMIN_READ_SCOPE)
				.antMatchers(HttpMethod.PUT, ADMIN_PATTERN).access(ADMIN_UPDATE_SCOPE)
				.antMatchers(HttpMethod.DELETE, ADMIN_PATTERN).access(ADMIN_DELETE_SCOPE);
	}

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setAccessTokenConverter(customAccessTokenConverter);

		String tokenKey = restTokenKeyFetcher.getTokenKey();

		converter.setVerifierKey(tokenKey);
		return converter;
	}

	@Bean
	@Primary
	public DefaultTokenServices tokenServices() {
		final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		return defaultTokenServices;
	}
}
