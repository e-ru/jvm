package eu.rudisch.authorizationserver.controller;

import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eu.rudisch.authorizationserver.token.CustomTokenStore;

@RestController
@RequestMapping("/tokens")
public class TokenController {

	@Resource(name = "tokenStore")
	private TokenStore tokenStore;

	@RequestMapping(method = RequestMethod.DELETE, value = "/refreshTokens")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void revokeRefreshToken(HttpServletRequest request, Authentication authentication,
			@RequestParam String username, @RequestParam(required = false) String clientId) {
		checkAuthentication(username, authentication);

		if (isSelfUser(username, authentication) || isOAuthAdmin(authentication)) {
			if (tokenStore instanceof CustomTokenStore) {
				((CustomTokenStore) tokenStore).removeRefreshToken(username, clientId);
			}
		} else {
			throw new InsufficientAuthenticationException("The user is not authenticated.");
		}
	}

	private void checkAuthentication(String username, Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()
				|| !(authentication.getPrincipal() instanceof String)) {
			throw new InsufficientAuthenticationException("The user is not authenticated.");
		}
	}

	private boolean isSelfUser(String username, Authentication authentication) {
		return username.equals((String) authentication.getPrincipal());
	}

	private boolean isOAuthAdmin(Authentication authentication) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		return authorities.stream().map(ga -> ga.getAuthority()).filter(sa -> "ROLE_oauth_admin".equals(sa)).findFirst()
				.isPresent();
	}
}
