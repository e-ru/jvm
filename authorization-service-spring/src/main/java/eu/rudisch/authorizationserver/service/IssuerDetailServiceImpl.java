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

	private JsonParser objectMapper = JsonParserFactory.create();

	private String issuerUsername;
	private List<String> authorities;

	@SuppressWarnings("unchecked")
	void setAuthorities(Object authorities) {
		this.authorities = (List<String>) authorities;
	}

	@Override
	public void extractDetails(Jwt jwt) {
		Map<String, Object> claims = objectMapper.parseMap(jwt.getClaims());
		issuerUsername = (String) claims.get(CLAIM_USER_NAME);
		setAuthorities(claims.get(CLAIM_AUTHORITIES));

		LOGGER.info(String.format("IssuerUsername %s, authorities %s", issuerUsername, authorities));
	}

	@Override
	public String getIssuerUsername() {
		return issuerUsername;
	}

	@Override
	public boolean checkRole(String role) {
		return authorities.contains(role);
	}
}
