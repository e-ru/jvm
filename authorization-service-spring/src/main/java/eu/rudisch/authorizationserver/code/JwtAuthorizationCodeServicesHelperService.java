package eu.rudisch.authorizationserver.code;

import java.util.Set;

import org.springframework.security.oauth2.provider.OAuth2Authentication;

public interface JwtAuthorizationCodeServicesHelperService {
	OAuth2Authentication oAuth2Authentication(String clientId, Set<String> scope, String redirectUrl, String user);
}
