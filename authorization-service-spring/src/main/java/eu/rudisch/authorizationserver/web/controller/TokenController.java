package eu.rudisch.authorizationserver.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eu.rudisch.authorizationserver.service.IssuerDetailService;
import eu.rudisch.authorizationserver.service.JwtExtractor;
import eu.rudisch.authorizationserver.token.CustomTokenStore;

@RestController
@RequestMapping("/tokens")
public class TokenController {

	private static final Logger LOGGER = LogManager.getLogger(TokenController.class);

	private static final String ROLE_OAUTH_ADMIN = "ROLE_oauth_admin";

	@Resource(name = "tokenStore")
	private TokenStore tokenStore;

	@Autowired
	private JwtExtractor jwtExtractor;

	@Autowired
	private IssuerDetailService issuerDetailService;

	@RequestMapping(method = RequestMethod.DELETE, value = "/refreshTokens")
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void revokeRefreshToken(HttpServletRequest request, @RequestParam String username,
			@RequestParam(name = "clientid", required = false) String clientId) {
		LOGGER.info(String.format("Revoke token of %s with client id %s", username, clientId));

		issuerDetailService.extractDetails(jwtExtractor.extract(request));
		if (username.equals(issuerDetailService.getIssuerUsername())
				|| issuerDetailService.checkRole(ROLE_OAUTH_ADMIN)) {
			if (tokenStore instanceof CustomTokenStore) {
				((CustomTokenStore) tokenStore).removeRefreshToken(username, clientId);
			}
		} else {
			throw new InsufficientAuthenticationException("The user is not authenticated.");
		}
	}
}
