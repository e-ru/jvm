package eu.rudisch.authorizationserver.controller;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eu.rudisch.authorizationserver.config.CustomTokenStore;

@RestController
@RequestMapping("/tokens")
public class TokenController {

	@Resource(name = "tokenStore")
	private TokenStore tokenStore;

	@RequestMapping(method = RequestMethod.DELETE, value = "/refreshTokens")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void revokeRefreshToken(@RequestParam String username,
			@RequestParam(required = false) String clientId) {
		if (tokenStore instanceof CustomTokenStore) {
			((CustomTokenStore) tokenStore).removeRefreshToken(username, clientId);
		}
	}
}
