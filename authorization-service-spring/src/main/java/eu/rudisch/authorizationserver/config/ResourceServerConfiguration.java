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

	private static final String RESOURCE_ID = "oauth2-control-resource";
	private static final String ADMIN_CREATE_SCOPE = "hasRole('ROLE_oauth_admin') and hasAuthority('create_oauth')";
	private static final String ADMIN_READ_SCOPE = "hasRole('ROLE_oauth_admin') and hasAuthority('read_oauth')";
	private static final String ADMIN_UPDATE_SCOPE = "hasRole('ROLE_oauth_admin') and hasAuthority('update_oauth')";
	private static final String ADMIN_DELETE_SCOPE = "hasRole('ROLE_oauth_admin') and hasAuthority('delete_oauth')";
	private static final String TOKEN_DELETE_SCOPE = "hasAuthority('revoke_refresh_token')";
//	private static final String SECURED_WRITE_SCOPE = "#oauth2.hasScope('WRITE')";
	private static final String ADMIN_PATTERN = "/admin/**";
	private static final String TOKENS_PATTERN = "/tokens/refreshTokens";

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(RESOURCE_ID);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.requestMatchers()
				.antMatchers(ADMIN_PATTERN, TOKENS_PATTERN)
				.and()
				.authorizeRequests()
				.antMatchers(HttpMethod.POST, ADMIN_PATTERN)
				.access(ADMIN_CREATE_SCOPE)
				.antMatchers(HttpMethod.GET, ADMIN_PATTERN)
				.access(ADMIN_READ_SCOPE)
				.antMatchers(HttpMethod.PUT, ADMIN_PATTERN)
				.access(ADMIN_UPDATE_SCOPE)
				.antMatchers(HttpMethod.DELETE, ADMIN_PATTERN)
				.access(ADMIN_DELETE_SCOPE)
				.antMatchers(HttpMethod.DELETE, TOKENS_PATTERN)
				.access(TOKEN_DELETE_SCOPE);
	}
}
