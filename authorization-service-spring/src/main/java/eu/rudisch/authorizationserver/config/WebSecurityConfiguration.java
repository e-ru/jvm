package eu.rudisch.authorizationserver.config;

import java.security.KeyPair;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import eu.rudisch.authorizationserver.Utils;
import eu.rudisch.authorizationserver.service.JwtExtractor;

@Configuration
@EnableWebSecurity
@Order(SecurityProperties.BASIC_AUTH_ORDER)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	public static final String TOKENS_PATTERN = "/tokens/refreshTokens";

	@Autowired
	private CustomProperties customProperties;

	@Autowired
	private JwtExtractor jwtExtractor;

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	protected AuthenticationManager getAuthenticationManager() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	KeyPair getKeyPair() {
		return Utils.genKeyPair(customProperties.getKeyStorePath(), customProperties.getKeyStorePassword(),
				customProperties.getKeyStoreAlias());
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(final HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers(HttpMethod.DELETE, TOKENS_PATTERN)
				.permitAll()
				.anyRequest().authenticated()
				.and()
				.addFilterAfter(new RefreshTokenFilter(jwtExtractor), UsernamePasswordAuthenticationFilter.class)
				.formLogin().permitAll()
				.and().csrf().disable()
				.logout()
				.logoutSuccessHandler((new CustomLogoutSuccessHandler()))
				.deleteCookies("JSESSIONID");
	}
}
