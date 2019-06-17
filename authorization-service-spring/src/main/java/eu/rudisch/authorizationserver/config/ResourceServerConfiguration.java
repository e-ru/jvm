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

	// TODO set useful resource id
	private static final String RESOURCE_ID = "inventory";
	private static final String SECURED_READ_SCOPE = "#oauth2.hasScope('READ')";
	private static final String SECURED_WRITE_SCOPE = "#oauth2.hasScope('WRITE')";
	private static final String SECURED_PATTERN = "/admin/**";

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) {
		resources.resourceId(RESOURCE_ID);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.requestMatchers()
				.antMatchers(SECURED_PATTERN)
				.and()
				.authorizeRequests()
				.antMatchers(HttpMethod.POST, SECURED_PATTERN)
				.access(SECURED_WRITE_SCOPE)
				.anyRequest()
				.access(SECURED_READ_SCOPE);
	}
}
