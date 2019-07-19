package eu.rudisch.authorizationserver.code;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthorizationCodeServicesHelperServiceImpl implements JwtAuthorizationCodeServicesHelperService {

	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private JdbcClientDetailsService jdbcClientDetailsService;

	Authentication authentication(String user) {
		return new CodeAuthentication(userDetailsService.loadUserByUsername(user));
	}

	OAuth2Request oAuth2Request(String clientId, Set<String> scope, String redirectUrl) {
		ClientDetails clientDetails = jdbcClientDetailsService.loadClientByClientId(clientId);
		return new OAuth2Request(null, clientId, null, false, scope, clientDetails.getResourceIds(), redirectUrl,
				null, null);
	}

	@Override
	public OAuth2Authentication oAuth2Authentication(String clientId, Set<String> scope, String redirectUrl,
			String user) {
		return new OAuth2Authentication(oAuth2Request(clientId, scope, redirectUrl), authentication(user));
	}

}
