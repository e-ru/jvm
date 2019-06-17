package eu.rudisch.authorizationserver.config;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
@EnableAuthorizationServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthorizationServerConfiguration implements AuthorizationServerConfigurer {

	// !!! https://github.com/adamzareba/company-structure-spring-security-oauth2-authorities

	// https://www.baeldung.com/spring-security-oauth-jwt

	// ( https://github.com/krish/microservices-course-on-youtube,
	// https://www.baeldung.com/rest-api-spring-oauth2-angular )

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private DataSource dataSource;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	public TokenStore tokenStore() {
//		return new JwtTokenStore(accessTokenConverter());
		return new JwtJdbcTokenStore(accessTokenConverter(), dataSource);
	}

	@Bean
	public TokenEnhancer tokenEnhancer() {
		return new CustomTokenEnhancer();
	}

	@Bean
	public CustomJwtAccessTokenConverter accessTokenConverter() {
		final CustomJwtAccessTokenConverter converter = new CustomJwtAccessTokenConverter();
		final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("mytest.jks"),
				"mypass".toCharArray());
		converter.setKeyPair(keyStoreKeyFactory.getKeyPair("mytest"));
		return converter;
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.checkTokenAccess("isAuthenticated()")
				.tokenKeyAccess("permitAll()")
				.allowFormAuthenticationForClients();
	}

	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.jdbc(dataSource).passwordEncoder(passwordEncoder);
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		tokenEnhancerChain.setTokenEnhancers(List.of(tokenEnhancer(), accessTokenConverter()));
		endpoints.tokenStore(tokenStore())
				.tokenEnhancer(tokenEnhancerChain)
				.authenticationManager(authenticationManager)
				.userDetailsService(userDetailsService);
	}
}
