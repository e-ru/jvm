package eu.rudisch.authorizationserver.service;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.oauth2.common.util.JsonParser;
import org.springframework.security.oauth2.common.util.JsonParserFactory;
import org.springframework.stereotype.Service;

@Service
public class IssuerDetailServiceImpl implements IssuerDetailService {

	private static final Logger LOGGER = LogManager.getLogger(IssuerDetailServiceImpl.class);

	private static final String CLAIM_USER_NAME = "user_name";
	private static final String CLAIM_AUTHORITIES = "authorities";
	private static final String ROLE_OAUTH_ADMIN = "ROLE_oauth_admin";

	private JsonParser objectMapper = JsonParserFactory.create();

	private String issuerUsername;
	private boolean oAuthAdmin;

	@Override
	public void extractDetails(Jwt jwt) {
		Map<String, Object> claims = objectMapper.parseMap(jwt.getClaims());
		issuerUsername = (String) claims.get(CLAIM_USER_NAME);
		@SuppressWarnings("unchecked")
		List<String> authorities = (List<String>) claims.get(CLAIM_AUTHORITIES);
		oAuthAdmin = authorities.contains(ROLE_OAUTH_ADMIN);

		LOGGER.info(String.format("IssuerUsername %s, isOAuthAdmin %b", issuerUsername, oAuthAdmin));
	}

	@Override
	public String getIssuerUsername() {
		return issuerUsername;
	}

	@Override
	public boolean isOAuthAdmin() {
		return oAuthAdmin;
	}
}
