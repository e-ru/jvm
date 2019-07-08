package eu.rudisch.authorizationadmin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import eu.rudisch.web.controller.resource.TokenKey;

@Component
public class RestTokenKeyFetcher {

	@Value("${custom.oauth.server}")
	private String oauthServer;

	String getTokenKey() {
		final RestTemplate restTemplate = new RestTemplate();
		TokenKey tokenKey = restTemplate.getForObject(oauthServer + "/oauth/token_key", TokenKey.class);
		return tokenKey.getValue();
	}

}
