package eu.rudisch.authorizationserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

	// hasRole: role of the user profile in database
	// hasAuthority: permission of user profile in database
	// #oauth2.hasScope: granted scope during access token creation

	private static final String RESOURCE_ID = "oauth2-control-resource";
	private static final String TOKENS_PATTERN = "/tokens/refreshTokens";

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(RESOURCE_ID);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.requestMatchers()
				.antMatchers(TOKENS_PATTERN)
				.and()
				.authorizeRequests()
				.antMatchers(HttpMethod.DELETE, TOKENS_PATTERN)
				.permitAll();
	}
}
